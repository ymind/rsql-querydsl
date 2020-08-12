package team.yi.rsql.querydsl.exception

class TypeNotSupportedException(message: String) : RsqlException(message) {
    companion object {
        private const val serialVersionUID = 5993677606796379281L
    }
}
