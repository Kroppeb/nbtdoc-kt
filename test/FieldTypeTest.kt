import parse.ast.*
import norswap.autumn.Autumn
import norswap.autumn.ParseOptions

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import parse.NBTDocParser

class FieldTypeTest {

	@Before
	fun setup() {
		val building = NBTDocParser.root
	}

	@Test
	fun string() {
		eq("string", FTString)
	}

	@Test
	fun number() {
		eq("double", FTNumber(NPTDouble(null)))
	}

	@Test
	fun numberRangeBoth() {
		eq("int @ 0..100", FTNumber(NPTInt(Range.Both(0, 100))))
	}

	@Test
	fun numberRangeUpper() {
		eq(
			"float @ ..0.15", FTNumber(
				NPTFloat(
					Range.High(0.15f)
				)
			)
		)
	}

	@Test
	fun number_range_lower() {
		eq(
			"long @ -7..", FTNumber(
				NPTLong(

					Range.Low(-7)
				)
			)
		)
	}

	@Test
	fun number_range_single() {
		eq(
			"short @ 0", FTNumber(
				NPTShort(

					Range.Single(0)
				)
			)
		)
	}

	@Test
	fun array_simple() {
		eq(
			"int[]", FTArray(
				NATInt(valueRange = null, lenRange = null)
			)
		)
	}

	@Test
	fun array_len_range() {
		eq(
			"int[] @ 0..4", FTArray(
				NATInt(
					valueRange = null,
					lenRange = Range.Both(0, 4)
				)
			)
		)
	}

	@Test
	fun array_value_range() {
		eq(
			"long @ ..20[]", FTArray(
				NATLong(
					valueRange = Range.High(20),
					lenRange = null
				)
			))
	}

	@Test
	fun array_both_range() {
		eq(
			"byte @ -20..30 [] @ 4..", FTArray(
				NATByte(
					valueRange = Range.Both(-20, 30),
					lenRange = Range.Low(4)
				)
			)
		)
	}

	@Test
	fun list_simple() {
		eq(
			"[boolean]", FTList(
				itemType = FTBoolean,
						lenRange = null
			)
		)
	}

	@Test
	fun list_nested() {
		eq(
			"[[[string]]]", FTList(
				itemType = FTList(
					itemType = FTList(
						itemType = FTString,
						lenRange = null
					),
					lenRange = null
				),
				lenRange = null
			)
		)
	}

	@Test
	fun list_range() {
		eq(
			"[int] @ 0..5", FTList(
				itemType = FTNumber(NPTInt(null)),
				lenRange = Range.Both(
					0, 5
				)
			)
		)
	}

	@Test
	fun named_simple() {
		eq(
			"FooBar", FTNamed(
				listOf(PathPart.Regular("FooBar"))
				
			) as FieldType
		)
	}

	@Test
	fun named_path() {
		eq(
			"super::module::FooBar", FTNamed(
				listOf(
					PathPart.Super,
					PathPart.Regular("module"),
					PathPart.Regular("FooBar")
				)
			)
		)
	}

	@Test
	fun id_type() {
		eq(
			"id(minecraft:item)", 
				FTId(
					Identifier("minecraft", "item")
			)
		)
		
	}

	@Test
	fun path_index() {
		eq("minecraft:item[id.super.field]", FTIndex (
			path= listOf(
			FieldPath.Child("id"),
			FieldPath.Super,
			FieldPath.Child("field")
			),
			target= Identifier("minecraft", "item")
		))
	}

	@Test
	fun or_type() {
		eq(
			"(int | boolean | byte @ 0..1)", FTOr(
				listOf(
					FTNumber(NPTInt(null)),
					FTBoolean,
					FTNumber(
						NPTByte(

							Range.Both(0, 1)
						)
					)
			)
		))
	}

	companion object {
		fun parse(str: String): FieldType {
			val result = Autumn.parse(NBTDocParser.fieldType, str, ParseOptions.get())
			assert(result.success){"Parsing was unsuccessful: $result\nerrormsg: ${result.error_message}"}
			assert(result.full_match){"Parsing was not complete: $result\nerrormsg: ${result.error_message}"}
			assert(result.value_stack.size == 1){"Parsing did not result in a size 1 stack! Actual size:${result.value_stack.size}\n${result.value_stack.toList()}"}
			return result.value_stack[0] as FieldType
		}

		fun eq(str: String, ft: FieldType) {
			assertEquals(ft, parse(str))
		}
	}

}