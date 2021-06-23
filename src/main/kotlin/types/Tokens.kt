package types

sealed class Token {
    override fun toString(): String {
        return this.javaClass.simpleName
    }

    object LCURLY : Token()
    object RCURLY : Token()
    object COMMA : Token()
    object LSQUARE : Token()
    object RSQUARE : Token()
    object COLON : Token()

    data class STRING(val string: String) : Token()

    data class BOOLEAN_LIT(val bool: Boolean) : Token()
    data class NUMBER_LIT(val number: String) : Token()
    object NULL_LIT : Token()

    object EOF : Token()
}