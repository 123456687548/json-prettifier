import java.io.File




fun main() {
    transformJsonFile(
        "./src/main/resources/jsonExampleWikipedia.json",
        "./src/main/resources/jsonOutput.json",
        ::minifiedJsonString
    )

}


fun transformJsonFile(
    srcFileName:String,
    dstFileName:String,
    transformer:(JsonDataType)->String
) {
    val srcFile = File(srcFileName)
    val uglyJsonString = srcFile.readText(Charsets.UTF_8)
    val parser = Parser(Lexer(uglyJsonString))
    val prettyJson = transformer(parser.parseJsonDataType())
    val dstFile = File(dstFileName)
    dstFile.writeText(prettyJson,Charsets.UTF_8)
}
