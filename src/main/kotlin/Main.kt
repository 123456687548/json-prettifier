@ExperimentalStdlibApi
fun main(args: Array<String>) {
    splitOutStream()

    val jsonString : String = if(args.isEmpty()){
        loadJsonFile()
    } else {
        loadJsonFile(args[0])
    }

    val lexer = Lexer(jsonString)
    val parser = Parser(lexer)

    val result = parser.parse().prettyPrint()

    saveJsonFile(result)
}