package types

import Lexer
import Parser
import util.loadJsonFile
import util.saveJsonFile
import java.io.FileNotFoundException
import kotlin.system.exitProcess

@ExperimentalStdlibApi
class Settings private constructor() {
    companion object {
        private val INSTANCE = Settings()
        private var init = false

        const val DEFAULT_INDENT_TYPE = "SPACE"
        const val DEFAULT_INDENT_AMOUNT = 4
        const val DEFAULT_IN_FILE = "in.json"
        const val DEFAULT_OUT_FILE = "out.json"

        fun get(): Settings {
            return INSTANCE
        }

        fun loadSettingsFile() {
            if (init) return

            try {
                val json = loadJsonFile("settings.json")
                val jsonObj = Parser(Lexer(json)).parse() as JsonDataType.JSON_OBJECT

                val indentTypeValue = jsonObj["indentType"]?.getValue()
                val indentAmountValue = jsonObj["indentAmount"]?.getValue()
                val inFileValue = jsonObj["inFile"]?.getValue()
                val outFileValue = jsonObj["outFile"]?.getValue()

                if (indentTypeValue != null && indentTypeValue is String) {
                    INSTANCE.indentType = IndentType.valueOf(indentTypeValue.toUpperCase())
                }

                if (indentAmountValue != null && indentAmountValue is Int) {
                    INSTANCE.indentAmount = indentAmountValue
                }

                if (inFileValue != null && inFileValue is String) {
                    INSTANCE.inFile = inFileValue
                }

                if (outFileValue != null && outFileValue is String) {
                    INSTANCE.outFile = outFileValue
                }

                init = true
            } catch (e: FileNotFoundException) {
                println("Settings file did not exist, auto generated it...")
                saveJsonFile(toJson().prettyPrint(), "settings.json")
                println("Stopping")
                exitProcess(0)
            }
        }

        private fun toJson(): JsonDataType.JSON_OBJECT {
            return JsonDataType.JSON_OBJECT(
                mapOf(
                    Pair(JsonDataType.JSON_STRING("indentType"), JsonDataType.JSON_STRING(DEFAULT_INDENT_TYPE)),
                    Pair(JsonDataType.JSON_STRING("indentAmount"), JsonDataType.JSON_NUMBER(DEFAULT_INDENT_AMOUNT.toString())),
                    Pair(JsonDataType.JSON_STRING("inFile"), JsonDataType.JSON_STRING(DEFAULT_IN_FILE)),
                    Pair(JsonDataType.JSON_STRING("outFile"), JsonDataType.JSON_STRING(DEFAULT_OUT_FILE))
                )
            )
        }
    }

    var indentType = IndentType.valueOf(DEFAULT_INDENT_TYPE)
    var indentAmount = DEFAULT_INDENT_AMOUNT
    var inFile = DEFAULT_IN_FILE
    var outFile = DEFAULT_OUT_FILE

    enum class IndentType(val indentChar: Char) {
        SPACE(' '),
        TAB('\t');
    }
}