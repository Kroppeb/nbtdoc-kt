package parse.ast

data class Field(val description: String, val name:String, val fieldType: FieldType)

sealed class CompoundSuper{
	data class Compound(val id: IdentPath) : CompoundSuper()
	data class Registry(val target:Identifier, val path: List<FieldPath>):CompoundSuper()
}

data class CompoundDef(val description: String, val name:String, val extend:CompoundSuper?, val fields: List<Field>)

data class DescribeDef(val id:IdentPath, val describeType:Identifier, val targets:List<Identifier>?)

data class UseDef(val export:Boolean, val path:IdentPath)

data class ModDef(val mod:String)

data class NbtDocFileDef(
	val uses: List<UseDef>,
	val compounds: List<CompoundDef>,
	val enums: List<Enum<*>>,
	val describes: List<DescribeDef>,
	val mods: List<ModDef>
){
	companion object{
		fun fromAny(all:Array<Any?>): NbtDocFileDef {
			val uses: MutableList<UseDef> = mutableListOf()
			val compounds: MutableList<CompoundDef> = mutableListOf()
			val enums: MutableList<Enum<*>> = mutableListOf()
			val describes: MutableList<DescribeDef> = mutableListOf()
			val mods: MutableList<ModDef> = mutableListOf()

			all.forEach{when(it){
				is UseDef -> uses.add(it)
				is CompoundDef -> compounds.add(it)
				is Enum<*> -> enums.add(it)
				is DescribeDef -> describes.add(it)
				is ModDef -> mods.add(it)
			} }

			return NbtDocFileDef(uses, compounds, enums, describes, mods)
		}
	}
}