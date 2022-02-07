package team.yi.rsql.querydsl.util

import com.querydsl.codegen.utils.StringUtils
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.core.types.dsl.PathBuilderValidator
import java.util.concurrent.ConcurrentHashMap

class PathFactory constructor(private val suffix: String = "") {
    private val paths: MutableMap<Class<*>, PathBuilder<*>> = ConcurrentHashMap()

    fun <T> create(type: Class<T>): PathBuilder<T> = create(type, PathBuilderValidator.PROPERTIES)

    fun <T> create(type: Class<T>, validator: PathBuilderValidator): PathBuilder<T> {
        @Suppress("UNCHECKED_CAST")
        var rv = paths[type] as? PathBuilder<T>

        if (rv == null) {
            rv = PathBuilder(type, variableName(type, suffix), validator)

            paths[type] = rv
        }

        return rv
    }

    companion object {
        fun variableName(type: Class<*>, suffix: String = ""): String = StringUtils.uncapitalize(type.simpleName) + suffix
    }
}
