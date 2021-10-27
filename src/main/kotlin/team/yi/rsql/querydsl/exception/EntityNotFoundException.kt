package team.yi.rsql.querydsl.exception

@Suppress("unused")
class EntityNotFoundException(
    message: String,
    val entityName: String?,
) : BuildException(message) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
