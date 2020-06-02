package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.RsqlOperator

interface FieldTypeHandler<E> {
    val node: ComparisonNode?
    val operator: RsqlOperator?
    val fieldMetadata: FieldMetadata
    val config: RsqlConfig<E>

    fun supportsType(type: Class<*>): Boolean
    fun getPath(parentPath: Expression<*>?): Expression<*>?
    fun getValue(values: List<String?>?, rootPath: Path<*>?, fm: FieldMetadata?): Collection<Expression<out Any?>?>?
    fun getExpression(path: Expression<*>, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression?
}
