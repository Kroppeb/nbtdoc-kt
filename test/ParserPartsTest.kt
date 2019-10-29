import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite
import parse.NBTDocParser

@RunWith(Suite::class)
@Suite.SuiteClasses(
	FieldTypeTest::class,
	CompoundDefTest::class,
	EnumTest::class,
	DescribeDefTest::class,
	ParseFileTest::class)
class ParserPartsTest{
	companion object {
		@BeforeClass
		@JvmStatic
		fun pre() {
			val p = NBTDocParser.parse("")
		}
	}
}