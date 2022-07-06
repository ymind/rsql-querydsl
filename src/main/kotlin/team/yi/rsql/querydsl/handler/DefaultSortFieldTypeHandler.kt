package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.Expressions
import team.yi.rsql.querydsl.FieldMetadata

class DefaultSortFieldTypeHandler(
    override val fieldMetadata: FieldMetadata,
) : SortFieldTypeHandler {
    override fun supports(type: Class<*>?): Boolean = true

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.path(fieldMetadata.clazz, parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }
}
