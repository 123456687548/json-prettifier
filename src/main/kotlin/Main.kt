
sealed class JsonDataType {
    data class JsonNumber(val num:String) : JsonDataType();
    data class JsonString(val str:String) : JsonDataType();
    data class JsonBoolean(val boolean: Boolean) : JsonDataType();
    data class JsonArray(val arr:List<JsonDataType>) : JsonDataType();
    data class JsonObject(val map:Map<String,JsonDataType>) : JsonDataType();
    object JsonNull : JsonDataType();
}

fun JsonDataType.toJsonString() : String {
    return when(this){
        is JsonDataType.JsonNumber -> this.num.toString()
        is JsonDataType.JsonString -> this.str
        is JsonDataType.JsonBoolean -> this.boolean.toString()
        is JsonDataType.JsonArray ->
            this.arr.joinToString(
                ",",
                "[",
                "]",
                -1,
                "..."
            ) { t -> t.toJsonString()  }

        is JsonDataType.JsonObject -> this.map.entries.joinToString(
            ",",
            "{",
            "}",
            -1,
            "...",
        ) {t -> "\"" + t.key + "\"" + ":" + t.value.toJsonString()}
        is JsonDataType.JsonNull -> "null"
    }
}




fun main(){

    val jsonExample = JsonDataType.JsonObject(
        mapOf(
            Pair("points", JsonDataType.JsonNumber("29.0")),
            Pair("score", JsonDataType.JsonNumber("41.0")),
            Pair("positions", JsonDataType.JsonArray(
                listOf(
                    JsonDataType.JsonNumber("21.0"),
                    JsonDataType.JsonNumber("66.2"),
                    JsonDataType.JsonBoolean(true),
                    JsonDataType.JsonNull
                )
            ))
        )
    )

    print(jsonExample.toJsonString());


}