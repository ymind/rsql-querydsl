package team.yi.rsql.querydsl.exception

import java.io.Serial

class TypeNotSupportedException(message: String) : RsqlException(message) {
    companion object {
        @Serial
        private const val serialVersionUID = 1L
    }
}
