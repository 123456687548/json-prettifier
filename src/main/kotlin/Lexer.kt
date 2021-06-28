import types.Token
import kotlin.jvm.Throws

@ExperimentalStdlibApi
class Lexer(input: String) {
    private val iter: PeekableIterator<Char> = PeekableIterator(input.iterator())
    private var lookahead: Token? = null

    fun next(): Token {
        lookahead?.let { lookahead = null; return it }
        consumeWhitespace()
        if (!iter.hasNext()) {
            return Token.EOF
        }

        return when (val c = iter.next()) {
            '{' -> Token.LCURLY
            '}' -> Token.RCURLY
            '[' -> Token.LSQUARE
            ']' -> Token.RSQUARE
            ':' -> Token.COLON
            ',' -> Token.COMMA
            '"' -> lexString()
            else -> lexDefault(c)
        }
    }

    @Throws(NotPermittedCharException::class, InvalidUnicodeEscapeSequenceException::class, UnterminatedStringException::class)
    private fun lexString(): Token {
        val result = StringBuilder()
        val unescapedUnicodeChars = mutableListOf<Int>()
        var index = -1
        var char: Char
        while (iter.hasNext()) {
            char = iter.next()
            index++

            if (char.code < 32) {
                throw NotPermittedCharException("Unescaped ASCII control characters are not permitted.")
            } else if (char == '\\') {
                char = iter.next()
                when (char) {
                    '\\' -> result.append('\\')
                    '"' -> result.append('"')
                    '/' -> result.append('/')
                    'b' -> result.append('\b')
                    't' -> result.append('\t')
                    'n' -> result.append('\n')
//                    'f' -> result.append('\f')
                    'r' -> result.append('\r')
                    'u' -> { //unicode escape
                        var charCode: Int
                        var temp = ""
                        for (i in 0..3) {
                            char = iter.next()
                            charCode = char.code

                            if (!((charCode in 48..57) || (charCode in 97..102) || (charCode in 65..70))) {
                                throw InvalidUnicodeEscapeSequenceException("Invalid Unicode escape sequence.")
                            }
                            temp += char
                        }

                        unescapedUnicodeChars.add(index)
                        result.append((Integer.parseInt(temp, 16)).toChar())
                    }
                }
            } else {
                if (char == '"') { // end of string
                    return if (unescapedUnicodeChars.isEmpty()) Token.STRING(result.toString())
                    else Token.STRING(result.toString(), unescapedUnicodeChars)
                }

                result.append(char)
            }
        }

        throw UnterminatedStringException("The String is not terminated")
    }

    private fun lexDefault(c: Char): Token {
        return when (c) {
            't',
            'f',
            'n' -> lexLiteral(c)
            else -> lexNumber(c)
        }
    }

    @Throws(NumberFormatException::class)
    private fun lexNumber(c: Char): Token {
        val result = StringBuilder(c.toString())
        var char: Char
        var isDecimal = false

        while (iter.hasNext()) {
            char = iter.peek()

            if (!(char.isDigit() || (char == '.' || char == 'e'))) {
                if (char == '.' && isDecimal) throw NumberFormatException("Number contains multiple dots")
                break
            }
            iter.next()
            if (char == '.' || char == 'e') isDecimal = true

            result.append(char)
        }

        return Token.NUMBER_LIT(result.toString())
    }

    @Throws(LiteralDoesNotExistException::class)
    private fun lexLiteral(c: Char): Token {
        val resultBuilder = StringBuilder(c.toString())

        for (i in 0..2) {
            if (!iter.hasNext()) throw LiteralDoesNotExistException("$resultBuilder Literal does not exist")
            resultBuilder.append(iter.next())
        }

        val result = resultBuilder.toString()

        if (result == "true") {
            return Token.BOOLEAN_LIT(true)
        } else if (result == "null") {
            return Token.NULL_LIT
        } else if (result == "fals" && iter.next() == 'e') {
            return Token.BOOLEAN_LIT(false)
        }
        throw LiteralDoesNotExistException("$result Literal does not exist")
    }

    fun peek(): Token {
        val token = next()
        lookahead = token
        return token
    }

    private fun consumeWhitespace() {
        while (iter.hasNext()) {
            val c = iter.peek()
            if (!c.isWhitespace()) break
            iter.next()
        }
    }

    class NotPermittedCharException(text: String) : Exception(text)
    class InvalidUnicodeEscapeSequenceException(text: String) : Exception(text)
    class UnterminatedStringException(text: String) : Exception(text)
    class LiteralDoesNotExistException(text: String) : Exception(text)

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
}