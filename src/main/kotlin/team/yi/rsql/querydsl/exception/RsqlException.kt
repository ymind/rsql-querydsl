package team.yi.rsql.querydsl.exception

import java.io.Serial

open class RsqlException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)

    companion object {
        @Serial
        private const val serialVersionUID = 1L
    }
}
