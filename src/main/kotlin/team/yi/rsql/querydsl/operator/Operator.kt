package team.yi.rsql.querydsl.operator

@Suppress("SpellCheckingInspection")
enum class Operator(vararg val rsqlOperator: String) {
    ISNULL("=isnull=", "=isNull="),
    ISNOTNULL("=notnull=", "=notNull=", "=isnotnull=", "=isNotNull="),
    IN("=in="),
    NOTIN("=notin=", "=notIn=", "=out="),
    EQUALS("=eq=", "=="),
    NOTEQUALS("=ne=", "!="),

    ISTRUE("=istrue=", "=isTrue="),
    ISFALSE("=isfalse=", "=isFalse="),

    ISEMPTY("=isempty=", "=isEmpty=", "=empty="),
    ISNOTEMPTY("=notempty=", "=notEmpty=", "=isnotempty=", "=isNotEmpty="),
    EQUALS_IGNORECASE("=eqic=", "=equalsignorecase=", "=equalsIgnoreCase="),
    NOTEQUALS_IGNORECASE("=noteqic=", "=notequalsignorecase=", "=notEqualsIgnoreCase="),
    LIKE("=like="),
    LIKE_IGNORECASE("=likeic=", "=likeignorecase=", "=likeIgnoreCase="),
    NOTLIKE("=notlike=", "=notLike="),

    MATCHES("=matches=", "=regex="),

    STARTWITH("=startsw=", "=startswith=", "=startsWith="),
    STARTWITH_IGNORECASE("=startswic=", "=startswithignorecase=", "=startsWithIgnoreCase="),
    ENDWITH("=endsw=", "=endswith=", "=endsWith="),
    ENDWITH_IGNORECASE("=endswic=", "=endswithignorecase=", "=endsWithIgnoreCase="),
    CONTAINS("=con=", "=contains="),
    CONTAINS_IGNORECASE("=conic=", "=containsignorecase=", "=containsIgnoreCase="),

    BETWEEN("=between="),
    NOTBETWEEN("=notbetween="),

    GREATER("=gt=", ">", "=greater="),
    GREATER_OR_EQUALS("=goe=", "=ge=", ">=", "=greaterorequals=", "=greaterOrEquals="),
    LESS_THAN("=lt=", "<", "=lessthan=", "=lessThan="),
    LESS_THAN_OR_EQUALS("=loe=", "=le=", "<=", "=lessthanorequals=", "=lessThanOrEquals="),

    BEFORE("=before="),
    AFTER("=after=");

    companion object {
        val lookup = mutableMapOf<String, Operator>()

        operator fun get(operator: String): Operator? = lookup[operator]

        init {
            values().forEach { it.rsqlOperator.forEach { symbol -> lookup[symbol] = it } }
        }
    }
}
