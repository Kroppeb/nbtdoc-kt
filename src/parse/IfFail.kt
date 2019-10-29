package parse

import norswap.autumn.Parse
import norswap.autumn.Parser
import norswap.autumn.ParserVisitor

class IfFail(val fail: Parser, val onFail: Parser): Parser(){
	/**
	 * Returns all the sub-parsers of this parser. Those are the parsers that this parser
	 * may call during the execution of its [.parse] method.
	 */
	override fun children(): Iterable<Parser> = listOf(fail, onFail)

	/**
	 * Override this method to implement the parsing logic.
	 *
	 *
	 * Returns true if and only if the parse succeeded.
	 *
	 *
	 * Must increase [Parse.pos] to indicate how much input was consumed, if any.
	 *
	 *
	 * If the parse failed and the method return false, [.parse] will take care of
	 * resetting [Parse.pos] to its original value on its own. Similarly, in case of failure
	 * [.parse] will also undo any side effects registered in  [Parse.log].
	 *
	 *
	 * Never call this directly, but call [.parse] instead.
	 */
	override fun doparse(parse: Parse): Boolean {
		val err0 = parse.error
		val errmsg0 = parse.error_message()
		val stk0 = parse.error_call_stack

		// if the child matches, #parse will undo its side effects
		if(fail.parse(parse)){
			return false
		}

		// negated parsers should not count towards the furthest error
		parse.error = err0
		//noinspection StringEquality
		if (parse.error_message() !== errmsg0)
			parse.set_error_message(errmsg0)
		parse.error_call_stack = stk0

		onFail.parse(parse)

		return true
	}

	/**
	 * Returns the full string representation of this parser (i.e. not only its rule name).
	 * The ouput may however reference sub-parsers by rule name.
	 */
	override fun toStringFull(): String = "parse.IfFail($fail, $onFail)"


	override fun accept(visitor: ParserVisitor) {
		onFail.accept(visitor)
	}
}