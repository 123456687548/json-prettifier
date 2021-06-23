package types

sealed class JsonDataType {
    open fun prettyPrint(indent: Int = 0): String = toString()

    data class JSON_STRING(val string: String) : JsonDataType() {
        @ExperimentalStdlibApi
        override fun toString(): String {
            val result = string
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r")

            val sb = StringBuilder()

            result.forEach {
                sb.append(replaceCharWithUnicode(it))
            }

            return "\"${sb}\""
        }
    }

    data class JSON_NUMBER(val number: String) : JsonDataType() {
        override fun toString(): String {
            return number
        }
    }

    data class JSON_BOOL(val bool: Boolean) : JsonDataType() {
        override fun toString(): String {
            return "$bool"
        }
    }

    object JSON_NULL : JsonDataType() {
        override fun toString(): String {
            return "null"
        }
    }

    data class JSON_OBJECT(val pairs: Map<JSON_STRING, JsonDataType>) : JsonDataType() {
        override fun prettyPrint(indent: Int): String {
            val result = StringBuilder("{\n")

            val iter = pairs.iterator()
            var current: Map.Entry<JSON_STRING, JsonDataType>

            while (iter.hasNext()) {
                current = iter.next()
                result.append("${getTabs(indent)}    ${current.key.prettyPrint(indent + 1)}: ${current.value.prettyPrint(indent + 1)}")
                if (iter.hasNext()) {
                    result.append(",\n")
                }
            }

            result.append("\n${getTabs(indent)}}")
            return result.toString()
        }
    }


    data class JSON_ARRAY(val array: List<JsonDataType>) : JsonDataType() {
        override fun prettyPrint(indent: Int): String {
            val result = StringBuilder("[\n")

            val iter = array.iterator()
            var current: JsonDataType

            while (iter.hasNext()) {
                current = iter.next()
                result.append("${getTabs(indent)}    ${current.prettyPrint(indent + 1)}")
                if (iter.hasNext()) {
                    result.append(",\n")
                }
            }

            result.append("\n${getTabs(indent)}]")
            return result.toString()
        }
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

@ExperimentalStdlibApi
fun replaceCharWithUnicode(char: Char): String {
    if (char.code > 255) { //todo bereich besser eingrenzen
        return "\\u${char.code.toString(16).uppercase()}"
    }
    return char.toString()
}