import java.io.File

fun loadJsonFile(filePath: String = "test.json"): String {
    val jsonFile = File(filePath)
    return String(jsonFile.readBytes())
}

fun saveJsonFile(json: String, filePath: String = "prettified.json") {
    val jsonFile = File(filePath)
    jsonFile.writeBytes(json.toByteArray())
}