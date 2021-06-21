import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

@ExperimentalStdlibApi
internal class ParserTest {

    @Test
    fun testStrings() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("\"\"", JsonDataType.JSON_STRING(""))
        testCase("\"t\"", JsonDataType.JSON_STRING("t"))
        testCase("\"Test\"", JsonDataType.JSON_STRING("Test"))
        testCase("\"1234 Test\"", JsonDataType.JSON_STRING("1234 Test"))
        testCase("\"1234 Test 1234\"", JsonDataType.JSON_STRING("1234 Test 1234"))
        testCase("\"1234 Tes2t 1234\"", JsonDataType.JSON_STRING("1234 Tes2t 1234"))
        testCase("\"hi \\u0066\"", JsonDataType.JSON_STRING("hi \\u0066"))
    }

    @Test
    fun testNumbers() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("1", JsonDataType.JSON_NUMBER(1))
        testCase("1.0", JsonDataType.JSON_DECIMAL_NUMBER(1.0))
        testCase("1.2", JsonDataType.JSON_DECIMAL_NUMBER(1.2))
        testCase("1.234", JsonDataType.JSON_DECIMAL_NUMBER(1.234))
        testCase("-1", JsonDataType.JSON_NUMBER(-1))
        testCase("-1.2", JsonDataType.JSON_DECIMAL_NUMBER(-1.2))
//        testCase("1e10", JsonDataType.JSON_NUMBER(1e10))
    }

    @Test
    fun testLiterals() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("true", JsonDataType.JSON_BOOL(true))
        testCase("false", JsonDataType.JSON_BOOL(false))
        testCase("null", JsonDataType.JSON_NULL)
    }

    @Test
    fun testArrays() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase("[]", JsonDataType.JSON_ARRAY(listOf()))
        testCase("[1]", JsonDataType.JSON_ARRAY(listOf(JsonDataType.JSON_NUMBER(1))))
        testCase("[1,2]", JsonDataType.JSON_ARRAY(listOf(JsonDataType.JSON_NUMBER(1), JsonDataType.JSON_NUMBER(2))))
        testCase(
            "[1,true,2,\"test\"]", JsonDataType.JSON_ARRAY(
                listOf(
                    JsonDataType.JSON_NUMBER(1), JsonDataType.JSON_BOOL(true), JsonDataType.JSON_NUMBER(2), JsonDataType.JSON_STRING("test")
                )
            )
        )
    }

    @Test
    fun testObjects() {
        println("Running test: ${object {}.javaClass.enclosingMethod.name}\n")

        testCase(
            "{\"a\" : \"b\"}",
            JsonDataType.JSON_OBJECT(
                mapOf(
                    Pair(
                        JsonDataType.JSON_STRING("a"),
                        JsonDataType.JSON_STRING("b")
                    )
                )
            )
        )

        testCase(
            "{\"object-inside-object\": {\n" +
                    "    \"sub-object\": {\n" +
                    "      \"val1\": \"val1\",\n" +
                    "      \"val2\": 2\n" +
                    "    }\n" +
                    "  }}",
            JsonDataType.JSON_OBJECT(
                mapOf(
                    Pair(
                        JsonDataType.JSON_STRING("object-inside-object"),
                        JsonDataType.JSON_OBJECT(
                            mapOf(
                                Pair(
                                    JsonDataType.JSON_STRING("sub-object"),
                                    JsonDataType.JSON_OBJECT(
                                        mapOf(
                                            Pair(
                                                JsonDataType.JSON_STRING("val1"),
                                                JsonDataType.JSON_STRING("val1"),
                                            ),
                                            Pair(
                                                JsonDataType.JSON_STRING("val2"),
                                                JsonDataType.JSON_NUMBER(2),
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
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

    private fun testCase(json: String, expectedResult: JsonDataType) {
        val parser = Parser(Lexer(json))
        val result = parser.parse()

        println("JSON: $json\nExpected: $expectedResult - Result: $result")

        assertEquals(expectedResult, result)
        println()
    }

    private fun testCase(json: String, expectedResult: List<JsonDataType>) {
        val parser = Parser(Lexer(json))
        val iter = expectedResult.iterator()

        println("JSON: $json\nExpected List: $expectedResult\n")

        while (iter.hasNext()) {
            val result = parser.parse()
            val expectedToken = iter.next()
            println("Expected: $expectedToken - Result: $result")
            assertEquals(expectedToken, result)
        }
        println()
    }

    private fun <T : Throwable> error(json: String, expectedResult: Class<T>) {
        val parser = Parser(Lexer(json))

        assertThrows(expectedResult) { parser.parse() }
    }
}