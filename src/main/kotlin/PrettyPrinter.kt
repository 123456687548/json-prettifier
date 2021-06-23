@ExperimentalStdlibApi
class PrettyPrinter(private val parser: Parser) {
    fun pretty(): String {
        val parsedValue = parser.parse()
        return parsedValue.prettyPrint()
    }
}

//todo settings for indent
fun getTabs(indent: Int): String {
    val retVal = StringBuilder()

    for (i in 1..indent) {
        retVal.append("    ")
    }

    return retVal.toString()
}

//todo aufräumen

@ExperimentalStdlibApi
fun main() {
    splitOutStream()
//    println(loadJsonFile("variableTest.json").replace("\\", "\\\\"))
    val lexer = Lexer(loadJsonFile("variableTest.json"))

//    val lexer = Lexer(loadJsonFile())
    val parser = Parser(lexer)

    val prettier = PrettyPrinter(parser)

    saveJsonFile(prettier.pretty())

//    benchmark(1000)
}

@ExperimentalStdlibApi
fun benchmark(tests: Int) {
    val resultList = mutableListOf<Long>()

    for (i in 1..tests) {
        val lexer = Lexer(loadJsonFile())
        val parser = Parser(lexer)

        val start = System.currentTimeMillis()

        val prettier = PrettyPrinter(parser)
        prettier.pretty()

        val end = System.currentTimeMillis()

        resultList.add(end - start)
        println("Prettiefier took: ${end - start} millisec.")
    }

    var average = 0L

    resultList.forEach {
        average += it
    }

    average /= tests

    println("average: $average")
}