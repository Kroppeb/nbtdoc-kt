package parse.ast

sealed class Range<T : Number> {
	data class Single<T : Number>(val value: T) : Range<T>(){
		override fun toString(): String = "Range($value)"
	}
	data class Low<T : Number>(val lowerBound: T) : Range<T>(){
		override fun toString(): String = "Range($lowerBound..)"
	}
	data class High<T : Number>(val higherBound: T) : Range<T>(){
		override fun toString(): String = "Range(..$higherBound)"
	}
	data class Both<T : Number>(val lowerBound: T, val higherBound: T) : Range<T>(){
		override fun toString(): String = "Range($lowerBound..$higherBound)"
	}
}