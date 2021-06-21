@ExperimentalStdlibApi
fun main(args: Array<String>) {
    splitOutStream()

    val json = loadJsonFile("variableTest.json")
    val lexer = Lexer(json)

    while (lexer.peek() != Token.EOF) {
        println(lexer.next())
    }
    println(lexer.next())
}