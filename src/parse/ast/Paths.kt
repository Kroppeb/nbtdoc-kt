package parse.ast



sealed class PathPart{
	object Root:PathPart()
	object Super:PathPart()
	data class Regular(val part:String):PathPart()
}

typealias IdentPath = List<PathPart>

data class Identifier(val namespace: String, val path: String)


sealed class FieldPath {
	object Super: FieldPath()
	data class Child(val path:String) : FieldPath()
}