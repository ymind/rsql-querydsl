package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import team.yi.rsql.querydsl.FieldMetadata

interface SortFieldTypeHandler {
    val fieldMetadata: FieldMetadata

    fun supports(type: Class<*>?): Boolean
    fun getPath(parentPath: Expression<*>?): Expression<*>?
}
