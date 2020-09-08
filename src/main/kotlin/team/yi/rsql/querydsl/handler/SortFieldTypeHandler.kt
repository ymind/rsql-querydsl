package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import team.yi.rsql.querydsl.FieldMetadata

interface SortFieldTypeHandler<E> {
    val fieldMetadata: FieldMetadata

    fun getPath(parentPath: Expression<*>?): Expression<*>?
}
