package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.operator.*
import java.sql.Time
import java.sql.Timestamp
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class TemporalFieldTypeHandler<E : Comparable<E>>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig,
) : ComparableFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
        return if (type == null) false else Date::class.java.isAssignableFrom(type) ||
            java.sql.Date::class.java.isAssignableFrom(type) ||
            Time::class.java.isAssignableFrom(type) ||
            Timestamp::class.java.isAssignableFrom(type)
    }

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = path as TemporalExpression<E>
        val right = values.orEmpty().distinct().map { it as TemporalExpression<E> }

        return when {
            operator.equals(Operator.AFTER) -> left.after(right[0])
            operator.equals(Operator.BEFORE) -> left.before(right[0])

            else -> super.getExpression(left, right, fm ?: fieldMetadata)
        }
    }
}
