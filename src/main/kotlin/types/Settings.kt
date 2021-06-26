package types

import Lexer
import Parser
import util.loadJsonFile
import util.saveJsonFile
import java.io.FileNotFoundException

@ExperimentalStdlibApi
class Settings private constructor() {
    companion object {
        private val INSTANCE = Settings()
        private var init = false

        fun get(): Settings {
            if (init) return INSTANCE

            val jsonObj = try {
                val json = loadJsonFile("settings.json")
                Parser(Lexer(json)).parse()
            } catch (e: FileNotFoundException) {
                toJson().also {
                    init = true
                    saveJsonFile(it.prettyPrint(), "settings.json")
                }
            } as JsonDataType.JSON_OBJECT

            val clazz = Settings::class.java

            val indentTypeField = clazz.getDeclaredField("indentType")
            val indentAmountField = clazz.getDeclaredField("indentAmount")
            val unicodeField = clazz.getDeclaredField("unicode")

            val indentTypeValue = jsonObj[indentTypeField.name]?.getValue()
            val indentAmountValue = jsonObj[indentAmountField.name]?.getValue()
            val unicodeValue = jsonObj[unicodeField.name]?.getValue()

            if (indentTypeValue != null && indentTypeValue is String) {
                indentTypeField.isAccessible = true
                indentTypeField.set(INSTANCE, IndentType.valueOf(indentTypeValue.toUpperCase()))
            }

            if (indentAmountValue != null && indentAmountValue is Int) {
                indentAmountField.isAccessible = true
                indentAmountField.set(INSTANCE, indentAmountValue)
            }

            if (unicodeValue != null && unicodeValue is Boolean) {
                unicodeField.isAccessible = true
                unicodeField.set(INSTANCE, unicodeValue)
            }

            init = true
            return INSTANCE
        }

        private fun toJson(): JsonDataType.JSON_OBJECT {
            return JsonDataType.JSON_OBJECT(
                mapOf(
                    Pair(JsonDataType.JSON_STRING("indentType"), JsonDataType.JSON_STRING(INSTANCE.indentType.toString())),
                    Pair(JsonDataType.JSON_STRING("indentAmount"), JsonDataType.JSON_NUMBER(INSTANCE.indentAmount.toString())),
                    Pair(JsonDataType.JSON_STRING("unicode"), JsonDataType.JSON_BOOL(INSTANCE.unicode)),
                )
            )
        }
    }

    val indentType: IndentType = IndentType.SPACE
    val indentAmount: Int = 4
    val unicode: Boolean = false

    enum class IndentType(val indentChar: Char) {
        SPACE(' '),
        TAB('\t');
    }
}