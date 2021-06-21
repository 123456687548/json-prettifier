@ExperimentalStdlibApi
class PrettyPrinter(private val parser: Parser) {
    fun pretty() : String{
        val parsedValue = parser.parse()
        val pretty = parsedValue.prettyPrint()
        println(pretty)
        return pretty
    }
}

fun getTabs(indent : Int) : String {
    var retVal = ""

    for (i in 1..indent){
        retVal += "\t"
    }

    return retVal
}


@ExperimentalStdlibApi
fun main() {
    splitOutStream()
//    println(loadJsonFile("variableTest.json").replace("\\", "\\\\"))
    val lexer = Lexer(loadJsonFile("variableTest.json"))
//    val lexer = Lexer(loadJsonFile())
    val parser = Parser(lexer)
    val prettier = PrettyPrinter(parser)
    saveJsonFile(prettier.pretty())
}