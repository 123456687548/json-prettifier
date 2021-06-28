package types

@ExperimentalStdlibApi
sealed class JsonDataType {
    open fun prettyPrint(indent: Int = 0): String = toString()
    open fun shortenPrint(): String = toString()

    abstract fun getValue(): Any?

    data class JSON_STRING(val string: String, val unescapedUnicodeChars: List<Int> = listOf()) : JsonDataType() {
        override fun toString(): String {
            var result = string
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r")

            if (Settings.get().unicode) {
                if (unescapedUnicodeChars.isNotEmpty()) {
                    unescapedUnicodeChars.asReversed().forEach {
                        result = result.replaceRange(it, it + 1, replaceCharWithUnicode(result[it]))
                    }
                }
            }

            return "\"${result}\""
        }

        override fun getValue(): Any {
            return string
        }
    }

    data class JSON_NUMBER(val number: String) : JsonDataType() {
        override fun toString(): String {
            return number
        }

        override fun getValue(): Any {
            if (number.contains('e') || number.contains('.')) {
                return number.toDouble()
            }

            return number.toInt()
        }
    }

    data class JSON_BOOL(val bool: Boolean) : JsonDataType() {
        override fun toString(): String {
            return "$bool"
        }

        override fun getValue(): Any {
            return bool
        }
    }

    object JSON_NULL : JsonDataType() {
        override fun toString(): String {
            return "null"
        }

        override fun getValue(): Any? {
            return null
        }
    }

    data class JSON_OBJECT(val pairs: Map<JSON_STRING, JsonDataType>) : JsonDataType() {
        override fun prettyPrint(indent: Int): String {
            val result = StringBuilder("{\n")

            val iter = pairs.iterator()
            var current: Map.Entry<JSON_STRING, JsonDataType>

            while (iter.hasNext()) {
                current = iter.next()
                result.append("${getIndent(indent + 1)}${current.key.prettyPrint(indent + 1)}: ${current.value.prettyPrint(indent + 1)}")
                if (iter.hasNext()) {
                    result.append(",\n")
                }
            }

            result.append("\n${getIndent(indent)}}")
            return result.toString()
        }

        override fun shortenPrint(): String {
            val result = StringBuilder("{")

            val iter = pairs.iterator()
            var current: Map.Entry<JSON_STRING, JsonDataType>

            while (iter.hasNext()) {
                current = iter.next()
                result.append("${current.key.shortenPrint()}:${current.value.shortenPrint()}")
                if (iter.hasNext()) {
                    result.append(",")
                }
            }

            result.append("}")
            return result.toString()
        }

        override fun getValue(): Any {
            return this
        }

        operator fun get(key: String): JsonDataType? = pairs[JSON_STRING(key)]
    }


    data class JSON_ARRAY(val array: List<JsonDataType>) : JsonDataType() {
        override fun prettyPrint(indent: Int): String {
            val result = StringBuilder("[\n")

            val iter = array.iterator()
            var current: JsonDataType

            while (iter.hasNext()) {
                current = iter.next()
                result.append("${getIndent(indent + 1)}${current.prettyPrint(indent + 1)}")
                if (iter.hasNext()) {
                    result.append(",\n")
                }
            }

            result.append("\n${getIndent(indent)}]")
            return result.toString()
        }

        override fun shortenPrint(): String {
            val result = StringBuilder("[")

            val iter = array.iterator()
            var current: JsonDataType

            while (iter.hasNext()) {
                current = iter.next()
                result.append(current.shortenPrint())
                if (iter.hasNext()) {
                    result.append(",")
                }
            }

            result.append("]")
            return result.toString()
        }

        override fun getValue(): Any {
            return this
        }
    }
}

@ExperimentalStdlibApi
fun getIndent(indent: Int): String {
    val retVal = StringBuilder()

    val indentChar = Settings.get().indentType.indentChar

    for (i in 1..indent) {
        for (a in 1..Settings.get().indentAmount) {
            retVal.append(indentChar)
        }
    }

    return retVal.toString()
}

@ExperimentalStdlibApi
fun replaceCharWithUnicode(char: Char): String {
    if (char.code > 255) {
        return "\\u${char.code.toString(16).uppercase()}"
    }
    return char.toString()
}