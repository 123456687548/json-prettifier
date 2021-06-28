import types.Settings
import util.loadJsonFile
import util.saveJsonFile
import util.splitOutStream
import java.lang.NumberFormatException

@ExperimentalStdlibApi
fun main(args: Array<String>) {
    splitOutStream()

    if (args.contains("--help")) {
        printHelp()
        return
    }

    if(args.contains("-sf")){
        Settings.loadSettingsFile()
    }

    if (args.contains("-i")) {
        val index = args.indexOf("-i")
        Settings.get().inFile = args[index + 1]
    }
    if (args.contains("-o")) {
        val index = args.indexOf("-o")
        Settings.get().outFile = args[index + 1]
    }
    if (args.contains("-it")) {
        val index = args.indexOf("-it")
        try {
            Settings.get().indentType = Settings.IndentType.valueOf(args[index + 1].toUpperCase())
        } catch (e: IllegalArgumentException) {
            printHelp()
            return
        }
    }
    if (args.contains("-ia")) {
        val index = args.indexOf("-ia")
        try {
            Settings.get().indentAmount = args[index + 1].toInt()
        } catch (e: NumberFormatException) {
            printHelp()
            return
        }
    }

    val jsonString = loadJsonFile(Settings.get().inFile)

    val result = if (args.contains("-s")) {
        Parser(Lexer(jsonString)).parse().shortenPrint()
    } else {
        Parser(Lexer(jsonString)).parse().prettyPrint()
    }

    saveJsonFile(result, Settings.get().outFile)
}

@ExperimentalStdlibApi
fun printHelp() {
    println("-------------- JSON PRETTIFIER --------------")
    println("-i <FilePath>         : Define input file")
    println("-o <FilePath>         : Define output file")
    println("-s                    : Shorten JSON")
    println("-it <IndentType>      : Define IndentType (\"${Settings.IndentType.SPACE}\" / \"${Settings.IndentType.TAB}\")")
    println("-ia <IndentAmount>    : Define IndentAmount")
    println("-sf                   : Use settings file (settings.json)")
    println("--help                : This help page")
    println()
    println("Default Values:")
    println("Default input file    : ${Settings.DEFAULT_IN_FILE}")
    println("Default output file   : ${Settings.DEFAULT_OUT_FILE}")
    println("Default indent type   : ${Settings.DEFAULT_INDENT_TYPE}")
    println("Default indent amount : ${Settings.DEFAULT_INDENT_AMOUNT}")
    println("---------------------------------------------")
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