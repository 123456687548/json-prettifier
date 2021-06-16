
// This might be a good reference for the tokens:
// https://www.json.org/json-en.html

sealed class Token {
    // Symbols
    object LeftBracket  : Token()   // [
    object RightBracket : Token()   // ]
    object LeftBrace    : Token()   // {
    object RightBrace   : Token()   // }
    object Colon        : Token()   // :
    object Comma        : Token()   // ,

    // Literals
    data class StringLiteral(val str: String)   : Token()
    data class BoolLiteral(val b: Boolean)      : Token()
    data class NumberLiteral(val num: String)   : Token()
    object NullLiteral : Token()

    // Control Token
    object EOF : Token()

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}

class PeekableIterator<A>(private val iter: Iterator<A>) {
    private var lookahead: A? = null
    fun next(): A {
        lookahead?.let { lookahead = null; return it }
        return iter.next()
    }

    fun peek(): A {
        val token = next()
        lookahead = token
        return token
    }

    fun hasNext(): Boolean {
        return lookahead != null || iter.hasNext()
    }
}

class Lexer(input:String){
    private val iter: PeekableIterator<Char> = PeekableIterator(input.iterator())
    private var lookahead: Token? = null

    private fun consumeWhitespace() {
        while (iter.hasNext()) {
            val c = iter.peek()
            if (!c.isWhitespace()) break
            iter.next()
        }
    }

    private fun ident(c: Char) : Token {
        var result = c.toString()
        while (iter.hasNext() && iter.peek().isJavaIdentifierPart()) { result += iter.next() }

        return when(result){
            "false" -> Token.BoolLiteral(false)
            "true" -> Token.BoolLiteral(true)
            "null" -> Token.NullLiteral
            else -> throw Exception("$result is not a valid identifier")
        }
    }

    private fun number(c: Char): Token {
        var result = c.toString()
        while (iter.hasNext() && iter.peek().isDigit()) { result += iter.next() }
        return Token.NumberLiteral(result)
    }

    private fun string(c: Char): Token {
        if(c != '\"') { print("Parsed String that doesnt begin correctly") }
        var result = ""
        while (iter.hasNext() && iter.peek() != '\"') { result += iter.next() }
        val strEnd = iter.next();
        if(strEnd != '\"'){ print("String did not end correctly\n"); }
        return Token.StringLiteral(result);
    }

    public fun peek(): Token {
        val token = next()
        lookahead = token
        return token
    }

    public fun next() : Token {
        lookahead?.let { lookahead = null; return it }

        consumeWhitespace()

        if (!iter.hasNext()) { return Token.EOF }

        return when (val nextchar = iter.next()){
            '{' -> Token.LeftBrace
            '}' -> Token.RightBrace
            ',' -> Token.Comma
            ':' -> Token.Colon
            '[' -> Token.LeftBracket
            ']' -> Token.RightBracket
            '\"' -> string(nextchar)
            else -> when {
                nextchar.isDigit() -> number(nextchar)
                else -> ident(nextchar)
            }
        }
    }

}
