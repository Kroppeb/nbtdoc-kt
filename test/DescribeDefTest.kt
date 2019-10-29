import parse.ast.DescribeDef
import parse.ast.Identifier
import parse.ast.PathPart
import norswap.autumn.Autumn
import norswap.autumn.ParseOptions
import org.junit.Assert
import org.junit.Test
import parse.NBTDocParser

class DescribeDefTest {

	@Test
	fun simple() {
		eq(
			str = "MyCompound describes minecraft:item[minecraft:stick, minecraft:tnt];",
			describe = DescribeDef(
				id = listOf(PathPart.Regular("MyCompound")),
				describeType = Identifier("minecraft", "item"),
				targets = listOf(
					Identifier("minecraft", "stick"),
					Identifier("minecraft", "tnt")
				)
			)
		)
	}

	@Test
	fun all() {
		eq(
			str = "MyCompound describes minecraft:block;",

			describe =
			DescribeDef(
				id = listOf(PathPart.Regular("MyCompound")),
				describeType = Identifier("minecraft", "block"),
				targets = null
			)
		)
	}

	companion object {
		fun parse(str: String): DescribeDef {
			val result = Autumn.parse(NBTDocParser.describeDef, str, ParseOptions.get())
			assert(result.success) { "Parsing was unsuccessful: $result\nerrormsg: ${result.error_message}" }
			assert(result.full_match) { "Parsing was not complete: $result\nerrormsg: ${result.error_message}" }
			assert(result.value_stack.size == 1) { "Parsing did not result in a size 1 stack! Actual size:${result.value_stack.size}\n${result.value_stack.toList()}" }
			return result.value_stack[0] as DescribeDef
		}

		fun eq(str: String, describe: DescribeDef) {
			Assert.assertEquals(describe, parse(str.trimMargin()))
		}
	}
}