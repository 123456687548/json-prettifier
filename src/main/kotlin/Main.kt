import java.io.File


fun main(){
    // TODO: currently its the other way around, we uglify the formatted json
    uglyJsonFileToPrettyJsonFile(
        "./src/main/kotlin/jsonExampleWikipedia.json",
        "./src/main/kotlin/jsonOutput.json"
    )
}



fun JsonDataType.toJsonString() : String {
    return when(this){
        is JsonDataType.JsonNumber -> this.num.toString()
        is JsonDataType.JsonString -> "\"" + this.str + "\""
        is JsonDataType.JsonBoolean -> this.boolean.toString()
        is JsonDataType.JsonArray ->
            this.arr.joinToString(
                ",\n",
                "[\n",
                "\n]",
                -1,
                "..."
            ) { t -> t.toJsonString()  }

        is JsonDataType.JsonObject -> this.map.entries.joinToString(
            ",\n",
            "{\n",
            "\n}",
            -1,
            "...",
        ) {t -> "\"" + t.key + "\"" + " : " + t.value.toJsonString()}
        is JsonDataType.JsonNull -> "null"
    }
}


fun uglyJsonFileToPrettyJsonFile(srcFileName:String, dstFileName:String) {

    val srcFile = File(srcFileName)
    val uglyJsonString = srcFile.readText(Charsets.UTF_8)

    val parser = Parser(Lexer(uglyJsonString))
    val prettyJson = parser.parseJsonDataType().toJsonString()
    val dstFile = File(dstFileName)

    dstFile.writeText(prettyJson,Charsets.UTF_8)
}








fun testObjectParse(){
    val parser = Parser(
        Lexer("{" +
                " \"name\" : \"juergen\" " + "}"
        )
    )
    print(parser.parseJsonDataType().toJsonString())
}

fun testJsonAst(){
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
    println(jsonExample)
}




