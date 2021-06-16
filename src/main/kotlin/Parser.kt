
// This is a good reference for the datatypes
// https://en.wikipedia.org/wiki/JSON#Data_types

sealed class JsonDataType {

   // Numbers in JSON are agnostic with regard to their representation within programming languages.
   // While this allows for numbers of arbitrary precision to be serialized, it may lead to portability issues.
   // Just Storing as Strings?
    data class JsonNumber(val num:String) : JsonDataType();

    data class JsonString(val str:String) : JsonDataType();
    data class JsonBoolean(val boolean: Boolean) : JsonDataType();
    data class JsonArray(val arr:List<JsonDataType>) : JsonDataType();
    data class JsonObject(val map:Map<String,JsonDataType>) : JsonDataType();
    object JsonNull : JsonDataType();
}



class Parser(private val tokens: Lexer) {

    fun parseJsonDataType() : JsonDataType {
        return tryParseJsonDataType() ?: throw Exception("Unexpected token ${tokens.peek()}")
    }

    private fun tryParseJsonDataType() : JsonDataType? {
        val nextToken = tokens.peek()
        return when (nextToken){
            Token.NullLiteral -> {
                tokens.next()
                JsonDataType.JsonNull
            }
            is Token.StringLiteral ->  {
                tokens.next()
                JsonDataType.JsonString(nextToken.str)
            }
            is Token.BoolLiteral -> {
                tokens.next()
                JsonDataType.JsonBoolean(nextToken.b)
            }
            is Token.NumberLiteral -> {
                tokens.next()
                JsonDataType.JsonNumber(nextToken.num)
            }
            Token.LeftBracket -> parseJsonArray()
            Token.LeftBrace -> parseJsonObject()
            else -> null
        }
    }

    private fun parseAttribute() : Pair<String,JsonDataType> {
        val key = expectNext<Token.StringLiteral>("string")
        expectNext<Token.Colon>("colon :")
        val value = parseJsonDataType()
        return Pair(key.str,value)
    }

    // TODO: Trailing commas are currently somehow allowed
    //       this bug might also be a feature
    //       but maybe parseJsonObject and parseJsonArray should be rewritten
    //       so that it detects it correctly
    // The JSON Data Interchange Syntax (PDF) (2nd ed.). Ecma International. December 2017. p. 11.
    // "A single comma token separates a value from a following name."
    // JSON disallows "trailing commas"

    private fun parseJsonObject() : JsonDataType.JsonObject {
        val attributes = mutableMapOf<String,JsonDataType>()
        expectNext<Token.LeftBrace>("{")
        while(true){

            val peeked = tokens.peek()
            if (peeked is Token.RightBrace){
                tokens.next()
                break;
            }
            val (key, value) = parseAttribute()
            attributes.put(key,value)

            val tok2 = tokens.next()
            if (tok2 is Token.RightBrace){ break; }
            else if(tok2 !is Token.Comma) {
                throw Exception(" expected , but found $tok2")
            }
        }
        return JsonDataType.JsonObject(attributes)
    }

    private fun parseJsonArray() : JsonDataType.JsonArray {
        val jsonValues = mutableListOf<JsonDataType>()
        expectNext<Token.LeftBracket>("[")

        while(true){
            val peeked = tokens.peek()
            if (peeked is Token.RightBracket){
                tokens.next()
                break;
            }
            jsonValues.add(parseJsonDataType())

            val tok2 = tokens.next()
            if (tok2 is Token.RightBracket){ break; }
            else if(tok2 !is Token.Comma) {
                throw Exception(" expected , but found $tok2")
            }
        }

        return JsonDataType.JsonArray(jsonValues)
    }

    private inline fun <reified A>expectNext(msg: String): A {
        val next = tokens.next()
        if (next !is A) {
            throw Exception("Unexpected token: expected $msg, but saw $next")
        }
        return next
    }

}