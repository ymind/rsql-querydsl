package team.yi.rsql.querydsl.operator

class RsqlOperator(vararg val symbols: String) {
    override fun hashCode(): Int = symbols.contentHashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return when (other) {
            is Operator -> other.rsqlOperator.intersect(setOf(*symbols)).isNotEmpty()
            is RsqlOperator -> other.symbols.intersect(setOf(*symbols)).isNotEmpty()
            else -> false
        }
    }

    companion object {
        @JvmStatic
        val equals = RsqlOperator(*Operator.EQUALS.rsqlOperator)

        @JvmStatic
        val notEquals = RsqlOperator(*Operator.NOTEQUALS.rsqlOperator)

        @Suppress("ObjectPropertyNaming")
        @JvmStatic
        val `in` = RsqlOperator(*Operator.IN.rsqlOperator)

        @JvmStatic
        val notIn = RsqlOperator(*Operator.NOTIN.rsqlOperator)
    }
}
