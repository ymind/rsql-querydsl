package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig

interface SortFieldTypeHandler<E> {
    val fieldMetadata: FieldMetadata
    val rsqlConfig: RsqlConfig<E>

    fun supportsType(type: Class<*>): Boolean
    fun getPath(parentPath: Expression<*>?): Expression<*>?
}
