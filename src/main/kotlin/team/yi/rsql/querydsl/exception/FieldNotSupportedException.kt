package team.yi.rsql.querydsl.exception

class FieldNotSupportedException(
    message: String,
    val type: Class<*>?,
    val fieldName: String?,
    val fieldSelector: String?
) : RsqlException(message) {
    companion object {
        private const val serialVersionUID = 5993677606796379281L
    }
}
