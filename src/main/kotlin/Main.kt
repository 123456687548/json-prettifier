import util.loadJsonFile
import util.saveJsonFile
import util.splitOutStream

@ExperimentalStdlibApi
fun main(args: Array<String>) {
    splitOutStream()

    val jsonString: String = if (args.isEmpty()) {
        loadJsonFile()
    } else {
//        util.loadJsonFile("variableTest.json")
        loadJsonFile(args[0])
    }

    val lexer = Lexer(jsonString)
    val parser = Parser(lexer)

    val result = parser.parse().prettyPrint()

    saveJsonFile(result)
}

@ExperimentalStdlibApi
fun benchmark(tests: Int) {
    val resultList = mutableListOf<Long>()

    for (i in 1..tests) {
        val lexer = Lexer(loadJsonFile())
        val parser = Parser(lexer)

        val start = System.currentTimeMillis()

        parser.parse().prettyPrint()

        val end = System.currentTimeMillis()

        resultList.add(end - start)
        println("Prettifier took: ${end - start} millisec.")
    }

    var average = 0L

    resultList.forEach {
        average += it
    }

    average /= tests

    println("average: $average")
}