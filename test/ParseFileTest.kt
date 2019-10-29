import parse.ast.*
import org.junit.Assert
import org.junit.Test
import parse.NBTDocParser
import java.io.File

class ParseFileTest {

	@Test
	fun smallFileRoot() {
		eqFile(
			"small_file_root.nbtdoc",
			NbtDocFileDef(
				uses = listOf(
					UseDef(
						export = true,
						path = listOf(
							PathPart.Regular(part = "small_file_sibling"),
							PathPart.Regular(part = "SpecificCompound")
						)
					)
				),
				compounds = listOf(
					CompoundDef(
						description = "",
						name = "MyCompound",
						extend = null,
						fields = listOf(
							Field(
								description = "", name = "Foo", fieldType = FTString
							)
						)
					)
				),
				enums = listOf(
					EByte(
						description = "",
						name = "MyEnum",
						values = listOf(
							EVByte(description = "", name = "VarOne", value = 1),
							EVByte(description = "", name = "VarTwo", value = 2),
							EVByte(description = "", name = "VarThree", value = 5)
						)
					)
				),
				describes = listOf(
					DescribeDef(
						id = listOf(PathPart.Regular(part = "MyCompound")),
						describeType = Identifier(namespace = "minecraft", path = "item"),
						targets = null
					)
				),
				mods = listOf(ModDef(mod = "small_file_sibling"))
			)
		)
	}

	@Test
	fun smallFileSibling() {
		eqFile(
			"small_file_sibling.nbtdoc",
			NbtDocFileDef(
				uses = listOf(
					UseDef(
						export = false,
						path = listOf(PathPart.Super, PathPart.Regular(part = "MyCompound"))
					),

					UseDef(
						export = false,
						path = listOf(
							PathPart.Super, PathPart.Regular(part = "MyEnum")
						)
					)

				),
				compounds = listOf(
					CompoundDef(
						description = "",
						name = "SpecificCompound",
						extend = CompoundSuper.Compound(listOf(
							PathPart.Regular(part = "MyCompound")
						)),
						fields = listOf(
							Field(description = "", name = "Bar", fieldType = FTBoolean)
							,
							Field(
								description = "",
								name = "bruh",
								fieldType = FTNamed(path = listOf(PathPart.Regular(part = "MyEnum")))
							)
						)
					)
				),
				enums = listOf(),
				describes = listOf(
					DescribeDef(
						id = listOf(
							PathPart.Regular(part = "SpecificCompound")
						),
						describeType = Identifier(namespace = "minecraft", path = "item"),
						targets = listOf(Identifier(namespace = "minecraft", path = "stick"))
					)
				),
				mods = listOf()
			)
		)
	}

	@Test
	fun fullFile() {
		eqFile(
			"full_file.nbtdoc",
			NbtDocFileDef(
				uses = listOf(
					UseDef(
						export = false,
						path = listOf(PathPart.Regular(part = "minecraft"), PathPart.Regular(part = "entity"))
					),
					UseDef(
						export = true,
						path = listOf(
							PathPart.Regular(part = "minecraft"),
							PathPart.Regular(part = "entity"),
							PathPart.Regular(part = "Villager")
						)
					)
				), compounds = listOf(
					CompoundDef(
						description = """ A mob which can be bred. It has no other unique NBT"""
						,
						name = "Breedable",
						extend = CompoundSuper.Compound(listOf(PathPart.Regular(part = "entity"), PathPart.Regular(part = "MobBase"))),
						fields = listOf(
							Field(
								description = """ If the animal has been fed"""
								, name = "InLove", fieldType = FTNumber(primitive = NPTInt(range = null))
							), Field(
								description = """ The age of the animal"""
								, name = "Age", fieldType = FTNumber(primitive = NPTInt(range = null))
							), Field(
								description = """ The age of the animal. Will not increment"""
								, name = "ForcedAge", fieldType = FTNumber(primitive = NPTInt(range = null))
							), Field(
								description = """ The UUIDLeast of the player who fed the animal"""
								, name = "LoveCauseLeast", fieldType = FTNumber(primitive = NPTLong(range = null))
							), Field(
								description = """ The UUIDMost of the player who fed the animal"""
								, name = "LoveCauseMost", fieldType = FTNumber(primitive = NPTLong(range = null))
							)
						)
					),
					CompoundDef(
						description = "",
						name = "Sheep",
						extend = CompoundSuper.Compound(listOf(PathPart.Regular(part = "Breedable"))),
						fields = listOf(
							Field(
								description = "", name = "Sheared", fieldType = FTBoolean
							),
							Field(
								description = "",
								name = "Color",
								fieldType = FTNamed(path = listOf(PathPart.Regular(part = "Color")))
							)
						)
					), CompoundDef(
						description = "",
						name = "Panda",
						extend = CompoundSuper.Compound(listOf(PathPart.Regular(part = "Breedable"))),
						fields = listOf(
							Field(
								description = """ The displayed gene
 If this gene is recessive '(r)' and 'HiddenGene' is not the same, the panda will display the 'normal' gene"""
								, name = "MainGene", fieldType = FTNamed(path = listOf(PathPart.Regular(part = "Gene")))
							), Field(
								description = """ The hidden gene"""
								,
								name = "HiddenGene",
								fieldType = FTNamed(path = listOf(PathPart.Regular(part = "Gene")))
							)
						)
					)
				),
				enums = listOf(
					EByte(
						description = "", name = "Color", values = listOf(
							EVByte(
								description = """ etc."""
								, name = "White", value = 0
							)
						)
					), EString(
						description = "", name = "Gene", values = listOf(
							EVString(
								description = """ The normal gene (d)"""
								, name = "Normal", value = "normal"
							), EVString(
								description = """ The lazy gene (d)"""
								, name = "Lazy", value = "lazy"
							), EVString(
								description = """ The worried gene (d)"""
								, name = "Worried", value = "worried"
							), EVString(
								description = """ The playful gene (d)"""
								, name = "Playful", value = "playful"
							), EVString(
								description = """ The brown gene (r)"""
								, name = "Brown", value = "brown"
							), EVString(
								description = """ The weak gene (r)"""
								, name = "Weak", value = "weak"
							), EVString(
								description = """ The aggressive gene (d)"""
								, name = "Aggressive", value = "aggressive"
							)
						)
					)
				),
				describes = listOf(
					DescribeDef(
						id = listOf(PathPart.Regular(part = "Breedable")),
						describeType = Identifier(namespace = "minecraft", path = "entity"),
						targets = listOf(
							Identifier(namespace = "minecraft", path = "cow"),
							Identifier(namespace = "minecraft", path = "pig")
						)
					),
					DescribeDef(
						id = listOf(PathPart.Regular(part = "Sheep")),
						describeType = Identifier(namespace = "minecraft", path = "entity"),
						targets = listOf(Identifier(namespace = "minecraft", path = "sheep"))
					),
					DescribeDef(
						id = listOf(PathPart.Regular(part = "Panda")),
						describeType = Identifier(namespace = "minecraft", path = "entity"),
						targets = listOf(Identifier(namespace = "minecraft", path = "panda"))
					)
				),
				mods = listOf()
			)

		)
	}

	companion object {
		fun parse(str: String): NbtDocFileDef {
			val result = NBTDocParser.parse(str)
			assert(result.success) { "Parsing was unsuccessful: $result\nerrormsg: ${result.error_message}" }
			assert(result.full_match) { "Parsing was not complete: $result\nerrormsg: ${result.error_message}" }
			assert(result.value_stack.size == 1) { "Parsing did not result in a size 1 stack! Actual size:${result.value_stack.size}\n${result.value_stack.toList()}" }
			return result.value_stack[0] as NbtDocFileDef
		}

		fun eq(str: String, nbtDocFile: NbtDocFileDef) {
			Assert.assertEquals(nbtDocFile, parse(str))
		}

		fun eqFile(file: String, nbtDocFile: NbtDocFileDef) =
			eq(File(this::class.java.getResource(file).file).readText(), nbtDocFile)
	}
}