package team.yi.rsql.querydsl.test.kotlintest.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.handler.FieldTypeHandler
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator

class CustomFieldTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : FieldTypeHandler<E> {
    override fun supports(type: Class<*>?): Boolean {
        return String::class.java == type
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.stringPath(parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isEmpty()) return null

        return values.map { Expressions.asSimple(it) }
    }

    @Suppress("INCOMPATIBLE_ENUM_COMPARISON", "UNCHECKED_CAST")
    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = path as StringExpression
        val right = values.orEmpty().distinct().map { it as StringExpression }.toTypedArray()
        val op = if (right.size == 1) {
            when (operator) {
                RsqlOperator.`in` -> RsqlOperator.equals
                RsqlOperator.notIn -> RsqlOperator.notEquals
                else -> operator
            }
        } else operator

        return when {
            op.equals(Operator.EQUALS_IGNORECASE) -> left.equalsIgnoreCase(right[0])
            op.equals(Operator.NOTEQUALS_IGNORECASE) -> left.notEqualsIgnoreCase(right[0])
            op.equals(Operator.LIKE) -> left.like(right[0])
            op.equals(Operator.LIKE_IGNORECASE) -> left.likeIgnoreCase(right[0])
            op.equals(Operator.STARTWITH) -> left.startsWith(right[0])
            op.equals(Operator.STARTWITH_IGNORECASE) -> left.startsWithIgnoreCase(right[0])
            op.equals(Operator.ENDWITH) -> left.endsWith(right[0])
            op.equals(Operator.ENDWITH_IGNORECASE) -> left.endsWithIgnoreCase(right[0])
            op.equals(Operator.ISEMPTY) -> left.isEmpty
            op.equals(Operator.ISNOTEMPTY) -> left.isNotEmpty
            op.equals(Operator.CONTAINS) -> left.contains(right[0])
            op.equals(Operator.CONTAINS_IGNORECASE) -> left.containsIgnoreCase(right[0])
            op.equals(Operator.IN) -> left.`in`(*right)
            op.equals(Operator.NOTIN) -> left.notIn(*right)
            else -> left.isNotEmpty
        }
    }
}
