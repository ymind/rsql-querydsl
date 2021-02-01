package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST")
abstract class ComparableFieldTypeHandler<E : Comparable<E>>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
        return if (type == null) false else Comparable::class.java.isAssignableFrom(type)
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        return values.map {
            val value = toComparable(it) ?: return null

            Expressions.asComparable(value)
        }.toList()
    }

    protected abstract fun toComparable(value: String?): Comparable<E>?

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = path as ComparableExpression<E>
        val right = values.orEmpty().distinct().map { it as ComparableExpression<E> }

        return when {
            operator.equals(Operator.BETWEEN) -> left.between(right[0], right[1])
            operator.equals(Operator.NOTBETWEEN) -> left.notBetween(right[0], right[1])

            operator.equals(Operator.GREATER) -> left.gt(right[0])
            operator.equals(Operator.GREATER_OR_EQUALS) -> left.goe(right[0])

            operator.equals(Operator.LESS_THAN) -> left.lt(right[0])
            operator.equals(Operator.LESS_THAN_OR_EQUALS) -> left.loe(right[0])

            else -> super.getExpression(left, right, fm ?: fieldMetadata)
        }
    }
}
