sealed class JsonDataType {
    open fun prettyPrint(indent : Int = 0): String = toString()

    data class JSON_STRING(val string: String) : JsonDataType() {
        override fun toString(): String {
            return "\"$string\""
        }
    }

    data class JSON_NUMBER(val number: Int) : JsonDataType() {
        override fun toString(): String {
            return "$number"
        }
    }

    data class JSON_DECIMAL_NUMBER(val number: Double) : JsonDataType() {
        override fun toString(): String {
            return "$number"
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
            var result = "{\n"

            val iter = pairs.iterator()
            var current: Map.Entry<JSON_STRING, JsonDataType>

            while (iter.hasNext()) {
                current = iter.next()
                result += "${getTabs(indent)}\t${current.key.prettyPrint(indent + 1)}: ${current.value.prettyPrint(indent + 1)}"
                if (iter.hasNext()) {
                    result += ",\n"
                }
            }

            result += "\n${getTabs(indent)}}"
            return result
        }
    }


    data class JSON_ARRAY(val array: List<JsonDataType>) : JsonDataType() {
        override fun prettyPrint(indent: Int): String {
            var result = "[\n"

            val iter = array.iterator()
            var current: JsonDataType

            while (iter.hasNext()) {
                current = iter.next()
                result += "${getTabs(indent)}\t${current.prettyPrint(indent + 1)}"
                if (iter.hasNext()) {
                    result += ",\n"
                }
            }

            result += "\n${getTabs(indent)}]"
            return result
        }
    }
}