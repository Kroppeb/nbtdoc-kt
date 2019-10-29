package parse

import parse.ast.*
import parse.ast.Enum
import parse.ast.NbtDocFileDef.Companion.fromAny
import norswap.autumn.Autumn
import norswap.autumn.DSL
import norswap.autumn.ParseOptions
import norswap.autumn.ParseResult
import norswap.autumn.parsers.CharPredicate


object NBTDocParser : DSL() {
	val root: rule

	val compoundDef: rule
	val enumDef: rule
	val describeDef: rule
	val useDef: rule
	val modDef: rule

	val fieldType: rule


	init {
		ws = empty
		val ssp = choice(
			set(" \n\t\r").at_least(1),
			seq("//", not("/"), until("\n"))
		)

		val sp = ssp.at_least(0)

		val sp1 = ssp.at_least(1)

		val intString = choice(
			"0",
			seq(
				opt("-"),
				set("1234579"),
				digit.at_least(0)
			)
		)

		val natString = choice(
			"0",
			seq(
				set("1234579"),
				digit.at_least(0)
			)
		)

		fun integer(convert: String.() -> Any) = intString.push(with_string { _, _, match -> match.convert() })


		val byte = integer(String::toByte)
		val short = integer(String::toShort)
		val int = integer(String::toInt)
		val long = integer(String::toLong)
		val natural = natString.push(with_string { _, _, match -> match.toInt() })

		val exp = opt(
			seq(
				set("eE"),
				set("+-").opt(),
				digit.at_least(1)
			)
		)

		val floatingString = seq(
			choice(
				seq(digit.at_least(1), opt(seq(".", digit.at_least(1)))),
				seq(".", digit.at_least(1))
			),
			exp
		)


		val uFloat =
			choice(
				word("Infinity").as_val(Float.POSITIVE_INFINITY),
				word("inf").as_val(Float.POSITIVE_INFINITY),
				word("NaN").as_val(Float.NaN),
				floatingString.push(with_string { _, _, m -> m.toFloat() })
			)
		val uDouble =
			choice(
				word("Infinity").as_val(Double.POSITIVE_INFINITY),
				word("inf").as_val(Double.POSITIVE_INFINITY),
				word("NaN").as_val(Double.NaN),
				floatingString.push(with_string { _, _, m -> m.toDouble() })
			)

		val float = choice(
			seq("-", uFloat).push { (b) -> -(b as Float) },
			uFloat
		)
		val double = choice(
			seq("-", uDouble).push { (b) -> -(b as Double) },
			uDouble
		)

		fun <T : Number> range(number: rule): rule = choice(
			seq(
				number,
				choice(
					seq(sp, "..", sp,
						choice(
							number.collect().lookback(1).push { (a, b) -> Range.Both<T>(a.t(), b.t()) },
							empty.collect().lookback(1).push { (a) -> Range.Low<T>(a.t()) }
						)
					),
					empty.collect().lookback(1).push { (a) -> Range.Single<T>(a.t()) }
				)
			),
			seq(sp, "..", sp, number).push { (a) -> Range.High<T>(a.t()) }
		)


		val byteRange = range<Byte>(byte)
		val shortRange = range<Short>(short)
		val intRange = range<Int>(int)
		val longRange = range<Long>(long)
		val floatRange = range<Float>(float)
		val doubleRange = range<Double>(double)
		val naturalRange = range<Int>(natural)


		val atbind = seq(sp, "@", sp)

		fun <T : Number> numberTypes(
			type: String,
			range: rule,
			convert: (Range<T>?) -> NumberPrimitiveType<T>
		) =
			seq(
				type,
				seq(
					atbind, range
				).opt()
			).push { FieldType.Number(convert(it.getOrNull(0).t())) }

		fun <T : Number> arrayType(
			type: String,
			range: rule,
			convert: (Range<T>?, Range<Int>?) -> NumberArrayType<T>
		) =
			seq(
				type,
				seq(
					atbind, range
				).opt().push { it.getOrNull(0) },
				sp, "[", sp, "]",
				seq(
					atbind, intRange
				).opt().push { it.getOrNull(0) }
			).push { (a, b) -> FieldType.Array(convert(a.t(), b.t())) }


		val ident = choice(alpha, "_").at_least(1).pushString()
		val sub = word("::")
		val identPath = seq(
			opt(sub.as_val(PathPart.Root)),
			(ident.push(with_string { _, _, m -> if (m == "super") PathPart.Super else PathPart.Regular(m) })).sep(
				1,
				sub
			)
		).push { it.toList() }

		fun basic(type: String, result: FieldType) = word(type).as_val(result)

		val mcValid = rule(mcValid).at_least(1)
		val mcIdent = seq(
			mcValid.push(with_string { _, _, m -> m }),
			":",
			mcValid.sep(1, "/").push(with_string { _, _, m -> m })
		).push { (a, b) -> Identifier(a.t(), b.t()) }

		val fieldPath =
			(ident.push(with_string { _, _, m -> if (m == "super") FieldPath.Super else FieldPath.Child(m) })).sep(
				1,
				"."
			).push { it.toList() }

		val mcIdentFieldPath = seq(mcIdent, "[", sp, fieldPath,  sp, "]")

		fieldType = choice(
			basic("boolean", FieldType.Boolean),
			basic("string", FieldType.String),

			// arrays first cause numbers are prefixes of arrays
			arrayType("byte", byteRange, ::NATByte),
			arrayType("int", intRange, ::NATInt),
			arrayType("long", longRange, ::NATLong),

			// numbers then
			numberTypes("byte", byteRange, ::NPTByte),
			numberTypes("short", shortRange, ::NPTShort),
			numberTypes("int", intRange, ::NPTInt),
			numberTypes("long", longRange, ::NPTLong),
			numberTypes("float", floatRange, ::NPTFloat),
			numberTypes("double", doubleRange, ::NPTDouble),

			// list
			seq(
				"[", sp, lazy { fieldType }, sp, "]",
				seq(atbind, naturalRange).opt()
			).push { FieldType.List(it[0].t(), it.getOrNull(1).t()) },

			// index
			mcIdentFieldPath.push { (a, b) -> FieldType.Index(a.t(), b.t()) },

			// id
			seq("id", sp, "(", sp, mcIdent, sp, ")").push { FieldType.Id(it.f()) },

			// named
			identPath.push { FTNamed(it.f()) },

			// or
			seq("(", sp, lazy { fieldType }.sep(1, seq(sp, "|", sp)), sp, ")")
				.push { FieldType.Or(it.map { a -> a as FieldType }) }
		)

		val docComment =
			seq("///", until("\n").pushString(), sp).at_least(0).push { it.joinToString("\n") }

		val quotedStr = seq(
			"\"",
			until(choice(controlChar, set("\\\"")))
				.sep(
					0, seq("\\", set("\\\"brtnf"))
				)
				.pushString(),
			"\""
		)

		val key = choice(quotedStr, ident)

		compoundDef = seq(
			docComment,
			"compound", sp1,
			ident,
			opt(seq(sp1, "extends", sp1, choice(
				mcIdentFieldPath.push { (m, f) -> CompoundSuper.Registry(m.t(), f.t()) },
				identPath.push { CompoundSuper.Compound(it.f()) }
			))).putOrNull(),
			sp, "{", sp,
			seq(docComment, sp, key, sp, ":", sp, fieldType)
				.push { (d, k, t) -> Field(d.t(), k.t(), t.t()) }
				.sep(0, seq(sp, ",", sp))
				.push { it.toList() }
			,
			sp, "}"
		)
			.push { (d, n, s, f) -> CompoundDef(d.t(), n.t(), s.t(), f.t()) }


		fun <T : Any> enumType(
			type: String,
			parser: rule,
			convertVal: (String, String, T) -> EnumVal<T>,
			convertType: (String, String, List<EnumVal<T>>) -> Enum<T>
		): rule? = seq(
			type, sp, ")", sp,
			ident, sp,
			"{", sp,
			seq(docComment, ident, sp, "=", sp, parser)
				.push { (d, n, v) -> convertVal(d.t(), n.t(), v.t()) }
				.sep(0, seq(sp, ",", sp))
				.push { it.toList() }
			,
			sp, "}"
		).collect().lookback(1).push { (d, n, v) -> convertType(d.t(), n.t(), v.t()) }


		enumDef = seq(
			docComment, sp, "enum", sp, "(", sp,
			choice(
				enumType("byte", byte, ::EVByte, ::EByte),
				enumType("short", short, ::EVShort, ::EShort),
				enumType("int", int, ::EVInt, ::EInt),
				enumType("long", long, ::EVLong, ::ELong),
				enumType("float", float, ::EVFloat, ::EFloat),
				enumType("double", double, ::EVDouble, ::EDouble),
				enumType("string", quotedStr, ::EVString, ::EString)
			)
		)

		describeDef = seq(
			identPath, sp1, "describes", sp1,
			mcIdent, sp,
			seq(
				"[", sp,
				mcIdent.sep(0, seq(sp, ",", sp)),
				sp, "]", sp
			).push { it.toList() }.optOrNull(),
			";"
		).push { (id, t, v) -> DescribeDef(id.t(), t.t(), v.t()) }

		useDef = seq(
			//seq("export", sp1).as_bool(),  <-- doesn'parse.t seem to work
			choice(seq("export", sp1, "use").as_val(true), word("use").as_val(false)),
			sp1, identPath, sp, ";"
		).push { (e, p) -> UseDef(e.t(), p.t()) }

		modDef = seq("mod", sp1, ident, sp, ";").push { ModDef(it.f()) }

		root = seq(
			sp, choice(
				compoundDef,
				enumDef,
				useDef,
				modDef,
				describeDef
			), sp
		).sep(0, sp).push(::fromAny)

		make_rule_names()
	}

	fun parse(input: String): ParseResult = Autumn.parse(root, input, ParseOptions.get())
	fun parse(input: List<String>): ParseResult = Autumn.parse(root, input, ParseOptions.get())

	private fun not(rule: String): rule = not(word(rule))
	private fun not(rule: rule): rule = rule.not()

	private fun anyBut(rule: String): rule = anyBut(word(rule))
	private fun anyBut(rule: rule): rule = rule(IfFail(rule.get(), any.get()))

	private fun until(rule: String): rule = until(word(rule))
	private fun until(rule: rule): rule = anyBut(rule).at_least(0)

	private fun opt(rule: String): rule = opt(word(rule))
	private fun opt(rule: rule): rule = rule.opt()

	private fun lazy(deferred: NBTDocParser.() -> rule): rule = super.lazy { deferred(this) }

	private fun rule.pushString() = collect().push_string_match()
	private fun rule.putOrNull() = push { it.getOrNull(0) }
	private fun rule.optOrNull() = opt().putOrNull()

}


@Suppress("UNCHECKED_CAST")
fun <T> Any?.t() = this as T

fun <T> Array<Any?>.f() = this[0] as T

val mcValid =
	CharPredicate("<alpha>") { 'a'.toInt() <= it && it <= 'z'.toInt() || it == '-'.toInt() || it == '_'.toInt() }

val controlChar = CharPredicate("<control>") { it.toChar().isISOControl() }
