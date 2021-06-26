import util.loadJsonFile
import util.saveJsonFile
import util.splitOutStream

val DEFAULT_IN_FILE = "in.json"
val DEFAULT_OUT_FILE = "out.json"

@ExperimentalStdlibApi
fun main(args: Array<String>) {
    splitOutStream()

    var jsonSourceFile: String? = null
    var outFile: String? = null

    if (args.contains("--help")) {
        println("-------------- JSON PRETTIFIER --------------")
        println("-i <FilePath>: Define input file")
        println("-o <FilePath>: Define output file")
        println("-s           : Shorten JSON")
        println("--help       : This help page")
        println()
        println("Default Values:")
        println("Default input file : $DEFAULT_IN_FILE")
        println("Default output file: $DEFAULT_OUT_FILE")
        println("---------------------------------------------")

        return
    }

    if (args.contains("-i")) {
        val index = args.indexOf("-i")
        jsonSourceFile = args[index + 1]
    }
    if (args.contains("-o")) {
        val index = args.indexOf("-o")
        outFile = args[index + 1]
    }

    val jsonString = loadJsonFile(jsonSourceFile ?: DEFAULT_IN_FILE)

    val result = if (args.contains("-s")) {
        Parser(Lexer(jsonString)).parse().shortenPrint()
    } else {
        Parser(Lexer(jsonString)).parse().prettyPrint()
    }

    saveJsonFile(result, outFile ?: DEFAULT_OUT_FILE)
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