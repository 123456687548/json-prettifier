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

    private fun lexString(): Token {
        var result = ""
        var char: Char
        while (iter.hasNext()) {
            char = iter.next()

            if (char.code < 32) {
                throw NotPermittedCharExeption("Unescaped ASCII control characters are not permitted.")
            } else if (char == '\\') {
                char = iter.next()
                when (char) {
                    '\\' -> result += '\\'
                    '"' -> result += '"'
                    '/' -> result += '/'
                    'b' -> result += '\b'
                    't' -> result += '\t'
                    'n' -> result += '\n'
//                    'f' -> result += '\f'
                    'r' -> result += '\r'
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
                        result += "\\u${temp}"
                    }
                }
            } else {
                if (char == '"') { // end of string
                    return Token.STRING(result)
                }

                result += char
            }
        }

        throw UnterminatedStringExeption("The String is not terminated")
    }

    private fun lexDefault(c: Char): Token {
        var char: Char

        return when (c) {
            't',
            'f',
            'n' ->  lexLiteral(c)
            else -> lexNumber(c)
        }
    }

    private fun lexNumber(c: Char): Token {
        var result = c.toString()

        throw NotANumberExeption("$result is not a Number")
    }

    private fun lexLiteral(c: Char): Token {
        var result = c.toString()

        for (i in 0..2) {
            if (!iter.hasNext()) throw LiteralDoesNotExistExeption("$result Literal does not exist")
            result += iter.next()
        }

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
    class NotANumberExeption(text: String) : Exception(text)
}