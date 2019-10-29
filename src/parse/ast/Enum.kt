package parse.ast


sealed class EnumVal<T:Any> {
	abstract val description: kotlin.String
	abstract val name: kotlin.String
	abstract val value : T

	data class Byte(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.Byte) : EnumVal<kotlin.Byte>()
	data class Short(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.Short) : EnumVal<kotlin.Short>()
	data class Int(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.Int) : EnumVal<kotlin.Int>()
	data class Long(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.Long) : EnumVal<kotlin.Long>()
	data class Float(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.Float) : EnumVal<kotlin.Float>()
	data class Double(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.Double) : EnumVal<kotlin.Double>()
	data class String(override val description: kotlin.String, override val name: kotlin.String, override val value: kotlin.String) : EnumVal<kotlin.String>()
}

typealias EVByte = EnumVal.Byte
typealias EVShort = EnumVal.Short
typealias EVInt = EnumVal.Int
typealias EVLong = EnumVal.Long
typealias EVFloat = EnumVal.Float
typealias EVDouble = EnumVal.Double
typealias EVString = EnumVal.String

sealed class Enum<T:Any>{
	abstract val description: kotlin.String
	abstract val name: kotlin.String
	abstract val values:List<EnumVal<T>>
	data class Byte(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.Byte>>): Enum<kotlin.Byte>()
	data class Short(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.Short>>): Enum<kotlin.Short>()
	data class Int(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.Int>>): Enum<kotlin.Int>()
	data class Long(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.Long>>): Enum<kotlin.Long>()
	data class Float(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.Float>>): Enum<kotlin.Float>()
	data class Double(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.Double>>): Enum<kotlin.Double>()
	data class String(override val description: kotlin.String, override val name: kotlin.String, override val values:List<EnumVal<kotlin.String>>): Enum<kotlin.String>()
}



typealias EByte = Enum.Byte
typealias EShort = Enum.Short
typealias EInt = Enum.Int
typealias ELong = Enum.Long
typealias EFloat = Enum.Float
typealias EDouble = Enum.Double
typealias EString = Enum.String