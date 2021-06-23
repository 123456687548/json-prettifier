import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

@ExperimentalStdlibApi
internal class LexerTest {
    @Test
    fun testStrings() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("\"\"", Token.STRING(""))
        testCase("\"t\"", Token.STRING("t"))
        testCase("\"Test\"", Token.STRING("Test"))
        testCase("\"1234 Test\"", Token.STRING("1234 Test"))
        testCase("\"1234 Test 1234\"", Token.STRING("1234 Test 1234"))
        testCase("\"1234 Tes2t 1234\"", Token.STRING("1234 Tes2t 1234"))
        testCase("\"hi \\u0066\"", Token.STRING("hi \\u0066"))
        testCase("\"This String contains \\\"!§\$%&/{}[]()=?\\@€|><,.-;:_+#\\\"\"\"", Token.STRING("This String contains \\\"!§\$%&/{}[]()=?\\@€|><,.-;:_+#\\\""))
    }

    @Test
    fun testNumbers() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("1", Token.NUMBER_LIT(1))
        testCase("1.0", Token.DECIMAL_NUMBER_LIT(1.0))
        testCase("1.2", Token.DECIMAL_NUMBER_LIT(1.2))
        testCase("1.234", Token.DECIMAL_NUMBER_LIT(1.234))
        testCase("-1", Token.NUMBER_LIT(-1))
        testCase("-1.2", Token.DECIMAL_NUMBER_LIT(-1.2))
//        testCase("1e10", Token.NUMBER_LIT(1e10))
    }

    @Test
    fun testLiterals() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("true", Token.BOOLEAN_LIT(true))
        testCase("false", Token.BOOLEAN_LIT(false))
        testCase("null", Token.NULL_LIT)
    }

    @Test
    fun testArrays() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase(
            "[]", listOf(
                Token.LSQUARE,
                Token.RSQUARE
            )
        )
        testCase(
            "[1]", listOf(
                Token.LSQUARE,
                Token.NUMBER_LIT(1),
                Token.RSQUARE
            )
        )
        testCase(
            "[1,2]", listOf(
                Token.LSQUARE,
                Token.NUMBER_LIT(1),
                Token.COMMA,
                Token.NUMBER_LIT(2),
                Token.RSQUARE
            )
        )
        testCase(
            "[1,true,2,\"test\"]", listOf(
                Token.LSQUARE,
                Token.NUMBER_LIT(1),
                Token.COMMA,
                Token.BOOLEAN_LIT(true),
                Token.COMMA,
                Token.NUMBER_LIT(2),
                Token.COMMA,
                Token.STRING("test"),
                Token.RSQUARE
            )
        )
    }

    @Test
    fun testObjects() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase(
            "{\"a\" : \"b\"}", listOf(
                Token.LCURLY,
                Token.STRING("a"),
                Token.COLON,
                Token.STRING("b"),
                Token.RCURLY
            )
        )

        testCase(
            "\"object-inside-object\": {\n" +
                    "    \"sub-object\": {\n" +
                    "      \"val1\": \"val1\",\n" +
                    "      \"val2\": 2\n" +
                    "    }\n" +
                    "  }", listOf(
                Token.STRING("object-inside-object"),
                Token.COLON,
                Token.LCURLY,
                Token.STRING("sub-object"),
                Token.COLON,
                Token.LCURLY,
                Token.STRING("val1"),
                Token.COLON,
                Token.STRING("val1"),
                Token.COMMA,
                Token.STRING("val2"),
                Token.COLON,
                Token.NUMBER_LIT(2),
                Token.RCURLY,
                Token.RCURLY
            )
        )
    }

    @Test
    fun testIlligals() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        error("tru", Lexer.LiteralDoesNotExistExeption::class.java)
        error("trfsdu", Lexer.LiteralDoesNotExistExeption::class.java)
        error("nsdf", Lexer.LiteralDoesNotExistExeption::class.java)
        error("fsdf", Lexer.LiteralDoesNotExistExeption::class.java)
    }

    private fun testCase(json: String, expectedResult: Token) {
        val lexer = Lexer(json)
        val result = lexer.next()

        println("JSON: $json\nExpected: $expectedResult - Result: $result")

        assertEquals(expectedResult, result)
        println()
    }

    private fun testCase(json: String, expectedResult: List<Token>) {
        val lexer = Lexer(json)
        val iter = expectedResult.iterator()

        println("JSON: $json\nExpected List: $expectedResult\n")

        while (iter.hasNext()) {
            val result = lexer.next()
            val expectedToken = iter.next()
            println("Expected: $expectedToken - Result: $result")
            assertEquals(expectedToken, result)
        }
        println()
    }

    private fun <T : Throwable> error(json: String, expectedResult: Class<T>) {
        val lexer = Lexer(json)

        assertThrows(expectedResult) { lexer.next() }
    }
}