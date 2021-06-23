import kotlin.jvm.Throws

@ExperimentalStdlibApi
class Parser(private val lexer: Lexer) {

    @Throws(ParseExeption::class)
    fun parse(): JsonDataType {
        return when (val token = lexer.peek()) {
            Token.LCURLY -> parseJsonObject()
            Token.LSQUARE -> parseJsonArray()
            is Token.STRING -> {
                lexer.next()
                JsonDataType.JSON_STRING(token.string)
            }
            is Token.BOOLEAN_LIT -> {
                lexer.next()
                JsonDataType.JSON_BOOL(token.bool)
            }
            is Token.NUMBER_LIT -> {
                lexer.next()
                JsonDataType.JSON_NUMBER(token.number)
            }
//            is Token.DECIMAL_NUMBER_LIT -> {
//                lexer.next()
//                JsonDataType.JSON_DECIMAL_NUMBER(token.number)
//            }
            Token.NULL_LIT -> {
                lexer.next()
                JsonDataType.JSON_NULL
            }
            else -> throw ParseExeption("")
        }
    }

    private fun parseAttribute(): Pair<JsonDataType.JSON_STRING, JsonDataType> {
        val key = parse()
        if (key !is JsonDataType.JSON_STRING) throw ParseExeption("key is not a string")
        expectNext<Token.COLON>("colon :")
        val value = parse()
        return Pair(key, value)
    }

    private fun parseJsonObject(): JsonDataType.JSON_OBJECT {
        val attributes = mutableMapOf<JsonDataType.JSON_STRING, JsonDataType>()
        expectNext<Token.LCURLY>("{")
        while (true) {
            val peekedToken = lexer.peek()
            if (peekedToken is Token.RCURLY) {
                lexer.next()
                break
            }

            val (key, value) = parseAttribute()
            attributes[key] = value

            val nextToken = lexer.next()
            if (nextToken is Token.RCURLY) {
                break
            } else if (nextToken !is Token.COMMA) {
                throw Exception(" expected , but found $nextToken")
            }
        }
        return JsonDataType.JSON_OBJECT(attributes)
    }

    private fun parseJsonArray(): JsonDataType.JSON_ARRAY {
        val jsonValues = mutableListOf<JsonDataType>()
        expectNext<Token.LSQUARE>("[")

        while (true) {
            val peekedToken = lexer.peek()
            if (peekedToken is Token.RSQUARE) {
                lexer.next()
                break
            }
            jsonValues.add(parse())

            val nextToken = lexer.next()
            if (nextToken is Token.RSQUARE) {
                break
            } else if (nextToken !is Token.COMMA) {
                throw Exception(" expected , but found $nextToken")
            }
        }

        return JsonDataType.JSON_ARRAY(jsonValues)
    }

    private inline fun <reified A> expectNext(msg: String): A {
        val next = lexer.next()
        if (next !is A) {
            throw Exception("Unexpected token: expected $msg, but saw $next")
        }
        return next
    }

    class ParseExeption(text: String) : Exception(text)
}

@ExperimentalStdlibApi
fun main() {
    splitOutStream()
//    val lexer = Lexer("{\"object-inside-object\": {\n" +
//            "    \"sub-object\": {\n" +
//            "      \"val1\": \"val1\",\n" +
//            "      \"val2\": 2\n" +
//            "    }\n" +
//            "  }}")
    val lexer = Lexer(loadJsonFile("variableTest.json"))
//    val lexer = Lexer(loadJsonFile())
    val parser = Parser(lexer)
    val jsonDataType = parser.parse()
    println(jsonDataType)
}