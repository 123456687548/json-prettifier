import kotlin.jvm.Throws

@ExperimentalStdlibApi
class Lexer(private val input: String) {
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

    @Throws(NotPermittedCharExeption::class, InvalidUnicodeEscapeSequenceExeption::class, UnterminatedStringExeption::class)
    private fun lexString(): Token {
        val result = StringBuilder()
        var char: Char
        while (iter.hasNext()) {
            char = iter.next()

            if (char.code < 32) {
                throw NotPermittedCharExeption("Unescaped ASCII control characters are not permitted.")
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
                    'u' -> { //unicode excape
                        var charCode: Int
                        var temp = ""
                        for (i in 0..3) {
                            char = iter.next()
                            charCode = char.code

                            if (!((charCode in 48..57) || (charCode in 97..102) || (charCode in 65..70))) {
                                throw InvalidUnicodeEscapeSequenceExeption("Invalid Unicode escape sequence.")
                            }
                            temp += char
                        }


                        result.append((Integer.parseInt(temp, 16)).toChar())
                    }
                }
            } else {
                if (char == '"') { // end of string
                    return Token.STRING(result.toString())
                }

                result.append(char)
            }
        }

        throw UnterminatedStringExeption("The String is not terminated")
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

    @Throws(LiteralDoesNotExistExeption::class)
    private fun lexLiteral(c: Char): Token {
        val resultBuilder = StringBuilder(c.toString())

        for (i in 0..2) {
            if (!iter.hasNext()) throw LiteralDoesNotExistExeption("$resultBuilder Literal does not exist")
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
        throw LiteralDoesNotExistExeption("$result Literal does not exist")
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

    class NotPermittedCharExeption(text: String) : Exception(text)
    class InvalidUnicodeEscapeSequenceExeption(text: String) : Exception(text)
    class UnterminatedStringExeption(text: String) : Exception(text)
    class LiteralDoesNotExistExeption(text: String) : Exception(text)
}