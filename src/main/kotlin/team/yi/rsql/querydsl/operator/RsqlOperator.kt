package team.yi.rsql.querydsl.operator

class RsqlOperator(vararg val symbols: String) {
    override fun hashCode(): Int = symbols.contentHashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return when (other) {
            is Operator -> other.rsqlOperator.intersect(arrayListOf(*symbols)).isNotEmpty()
            is RsqlOperator -> other.symbols.intersect(arrayListOf(*symbols)).isNotEmpty()
            else -> false
        }
    }
}
