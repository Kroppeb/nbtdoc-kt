import parse.ast.*
import norswap.autumn.Autumn
import norswap.autumn.ParseOptions

import org.junit.Assert.*
import org.junit.Test
import parse.NBTDocParser

class CompoundDefTest {
	@Test
	fun simple() {
		eq(
			str =
			"""|compound Foo {
			   |	field_one: int,
			   |	field_two: string
			   |}""",
			compound = CompoundDef(
				description = "",
				name = "Foo",
				extend = null,
				fields = listOf(
					Field(
						description = "",
						name = "field_one",
						fieldType = FTNumber(NPTInt(null))
					),
					Field(
						description = "",
						name = "field_two",
						fieldType = FTString
					)
				)
			)
		)
	}

	@Test
	fun doc_comments() {
		eq(
			str =
			"""|/// This is my compound
			   |compound Foo {
			   |	/// Hello World
			   |	field: byte
			   |}""",

			compound = CompoundDef(
				description = " This is my compound",
				name = "Foo",
				extend = null,
				fields = listOf(
					Field(
						description = " Hello World",
						name = "field",
						fieldType = FTNumber(NPTByte(null))
					)
				)
			)
		)
	}


	@Test
	fun extends() {
		eq(
			str =
			"""|compound Foo extends some::module::Bar {
			   |	field: boolean
			   |}""",

			compound = CompoundDef
				(
				description = "",
				name = "Foo",
				extend = CompoundSuper.Compound(
					listOf(
						PathPart.Regular("some"),
						PathPart.Regular("module"),
						PathPart.Regular("Bar")
					)
				),
				fields = listOf(

					Field(
						description = "",
						name = "field",
						fieldType = FTBoolean
					)
				)
			)
		)

	}

	@Test
	fun extendRegistry() {
		eq(
			str =
			"""|compound Foo extends namespace:registry[id] {
			   |	field: id(foo:bar/baz)
			   |}""",
			compound = CompoundDef(
				description = "",
				name = "Foo",
				fields = listOf(
					Field(
						description = "",
						name = "field",
						fieldType = FTId(
							Identifier(
								namespace = "foo",
								path = "bar/baz"
							)
						)
					)
				),

				extend = CompoundSuper.Registry
					(
					target = Identifier("namespace", "registry"),
					path = listOf(
						FieldPath.Child("id")
					)
				)
			)
		)
	}

	companion object {
		fun parse(str: String): CompoundDef {
			val result = Autumn.parse(NBTDocParser.compoundDef, str, ParseOptions.get())
			assert(result.success) { "Parsing was unsuccessful: $result\nerrormsg: ${result.error_message}" }
			assert(result.full_match) { "Parsing was not complete: $result\nerrormsg: ${result.error_message}" }
			assert(result.value_stack.size == 1) { "Parsing did not result in a size 1 stack! Actual size:${result.value_stack.size}\n${result.value_stack.toList()}" }
			return result.value_stack[0] as CompoundDef
		}

		fun eq(str: String, compound: CompoundDef) {
			assertEquals(compound, parse(str.trimMargin()))
		}
	}
}