


fun minifiedJsonString(json:JsonDataType): String {
    return when(json){
        is JsonDataType.JsonNumber -> json.num.toString()
        is JsonDataType.JsonString -> "\""+ json.str + "\""
        is JsonDataType.JsonBoolean -> json.boolean.toString()
        is JsonDataType.JsonArray ->
            json.arr.joinToString(
                ",",
                "[",
                "]",
                -1,
                "..."
            ) { t -> minifiedJsonString(t) }
        is JsonDataType.JsonObject -> json.map.entries.joinToString(
            ",",
            "{",
            "}",
            -1,
            "..."
        ) { t -> "\"" + t.key + "\"" + ":" + minifiedJsonString(t.value) }
        JsonDataType.JsonNull -> "null"
    }
}
