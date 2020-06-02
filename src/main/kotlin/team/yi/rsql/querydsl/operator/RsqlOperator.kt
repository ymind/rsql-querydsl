package team.yi.rsql.querydsl.operator

class RsqlOperator {
    val symbols: Array<String>
    var name: String? = null
        private set

    constructor(symbol: String) {
        symbols = arrayOf(symbol)
    }

    constructor(symbols: Array<String>) {
        this.symbols = symbols
    }

    constructor(name: String?, symbols: Array<String>) {
        this.name = name
        this.symbols = symbols
    }

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
