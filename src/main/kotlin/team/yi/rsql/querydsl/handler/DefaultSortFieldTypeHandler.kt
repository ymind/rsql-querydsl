package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.Expressions
import team.yi.rsql.querydsl.FieldMetadata

class DefaultSortFieldTypeHandler<E>(
    override val fieldMetadata: FieldMetadata,
) : SortFieldTypeHandler<E> {
    override fun supports(type: Class<*>): Boolean = true

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.path(fieldMetadata.type, parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }
}
