import parse.ast.*
import parse.ast.Enum
import norswap.autumn.Autumn
import norswap.autumn.ParseOptions

import org.junit.Assert.*
import org.junit.Test
import parse.NBTDocParser

class EnumTest {

	@Test
	fun simple() {
		eq(
			str =
			"""|enum (int) MyEnum {
			   |	VarOne = 1,
			   |	VarTwo = 2,
			   |	VarThree = 5
			   |}""",

			enum = EInt(
				description = "",
				name = "MyEnum",
				values = listOf(
					EVInt(
						description = "",
						name = "VarOne",
						value = 1
					),
					EVInt(
						description = "",
						name = "VarTwo",
						value = 2
					),
					EVInt(
						description = "",
						name = "VarThree",
						value = 5
					)
				)
			)
		)
	}


	@Test
	fun docComments() {
		eq(
			str =
			"""|/// My Enum
			   |enum(int) MyEnum {
			   |	/// var one
			   |	VarOne = 1,
			   |	/// var two
			   |	VarTwo = 2,
			   |	VarThree = 5
			   |}"""
			,
			enum = EInt(
				description = " My Enum",
				name = "MyEnum",
				values = listOf(
					EVInt(
						description = " var one",
						name = "VarOne",
						value = 1
					),
					EVInt(
						description = " var two",
						name = "VarTwo",
						value = 2
					),
					EVInt(
						description = "",
						name = "VarThree",
						value = 5
					)
				)
			)
		)
	}

	@Test
	fun gene(){
		eq(str=
		"""|enum(string) Gene {
		   |	/// The normal gene (d)
		   |	Normal = "normal",
		   |	/// The lazy gene (d)
		   |	Lazy = "lazy",
		   |	/// The worried gene (d)
		   |	Worried = "worried",
		   |	/// The playful gene (d)
		   |	Playful = "playful",
		   |	/// The brown gene (r)
		   |	Brown = "brown",
		   |	/// The weak gene (r)
		   |	Weak = "weak",
		   |	/// The aggressive gene (d)
		   |	Aggressive = "aggressive"
		   |}""",
			enum = EString(
				description = "",
				name = "Gene",
				values = listOf(
					EVString(description = " The normal gene (d)", name="Normal", value = "normal"),
					EVString(description = " The lazy gene (d)", name="Lazy", value = "lazy"),
					EVString(description = " The worried gene (d)", name="Worried", value = "worried"),
					EVString(description = " The playful gene (d)", name="Playful", value = "playful"),
					EVString(description = " The brown gene (r)", name="Brown", value = "brown"),
					EVString(description = " The weak gene (r)", name="Weak", value = "weak"),
					EVString(description = " The aggressive gene (d)", name="Aggressive", value = "aggressive")
				)
			)
		)
	}


	companion object {
		fun parse(str: String): Enum<*> {
			val result = Autumn.parse(NBTDocParser.enumDef, str, ParseOptions.get())
			assert(result.success) { "Parsing was unsuccessful: $result\nerrormsg: ${result.error_message}" }
			assert(result.full_match) { "Parsing was not complete: $result\nerrormsg: ${result.error_message}" }
			assert(result.value_stack.size == 1) { "Parsing did not result in a size 1 stack! Actual size:${result.value_stack.size}\n${result.value_stack.toList()}" }
			return result.value_stack[0] as Enum<*>
		}

		fun eq(str: String, enum: Enum<*>) {
			assertEquals(enum, parse(str.trimMargin()))
		}
	}
}