import java.io.File

fun loadJsonFile(filePath: String = "test.json"): String {
    val jsonFile = File(filePath)
    return jsonFile.readText()
}

fun saveJsonFile(json: String, filePath: String = "prettified.json") {
    val jsonFile = File(filePath)
    jsonFile.writeText(json)
}