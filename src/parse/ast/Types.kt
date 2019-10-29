package parse.ast

sealed class NumberPrimitiveType<T:Number> {
	abstract val range:Range<T>?
	data class Byte(override val range: Range<kotlin.Byte>?) : NumberPrimitiveType<kotlin.Byte>()
	data class Short(override val range: Range<kotlin.Short>?) : NumberPrimitiveType<kotlin.Short>()
	data class Int(override val range: Range<kotlin.Int>?) : NumberPrimitiveType<kotlin.Int>()
	data class Long(override val range: Range<kotlin.Long>?) : NumberPrimitiveType<kotlin.Long>()
	data class Float(override val range: Range<kotlin.Float>?) : NumberPrimitiveType<kotlin.Float>()
	data class Double(override val range: Range<kotlin.Double>?) : NumberPrimitiveType<kotlin.Double>()
}

typealias NPTByte = NumberPrimitiveType.Byte
typealias NPTShort = NumberPrimitiveType.Short
typealias NPTInt = NumberPrimitiveType.Int
typealias NPTLong = NumberPrimitiveType.Long
typealias NPTFloat = NumberPrimitiveType.Float
typealias NPTDouble = NumberPrimitiveType.Double


sealed class NumberArrayType<T:Number> {
	abstract val valueRange:Range<T>?
	abstract val lenRange: Range<kotlin.Int>?
	data class Byte(override val valueRange: Range<kotlin.Byte>?, override val lenRange: Range<kotlin.Int>?) : NumberArrayType<kotlin.Byte>()
	data class Int(override val valueRange: Range<kotlin.Int>?, override val lenRange: Range<kotlin.Int>?) : NumberArrayType<kotlin.Int>()
	data class Long(override val valueRange: Range<kotlin.Long>?, override val lenRange: Range<kotlin.Int>?) : NumberArrayType<kotlin.Long>()
}

typealias NATByte = NumberArrayType.Byte
typealias NATInt = NumberArrayType.Int
typealias NATLong = NumberArrayType.Long

sealed class FieldType{
	object Boolean:FieldType()
	data class Number<T:kotlin.Number>(val primitive: NumberPrimitiveType<T>) : FieldType()
	object String:FieldType()
	data class Array<T:kotlin.Number>(val primitive: NumberArrayType<T>) : FieldType()
	data class List(val itemType:FieldType, val lenRange: Range<Int>?) : FieldType()
	data class Named(val path: IdentPath) : FieldType()
	data class Index(val target: Identifier, val path: kotlin.collections.List<FieldPath>) : FieldType()
	data class Id(val id: Identifier) : FieldType()
	data class Or(val options: kotlin.collections.List<FieldType>) : FieldType()
}

typealias FTBoolean = FieldType.Boolean
typealias FTNumber<T> = FieldType.Number<T>
typealias FTString = FieldType.String
typealias FTArray<T> = FieldType.Array<T>
typealias FTList = FieldType.List
typealias FTNamed = FieldType.Named
typealias FTIndex = FieldType.Index
typealias FTId = FieldType.Id
typealias FTOr = FieldType.Or


