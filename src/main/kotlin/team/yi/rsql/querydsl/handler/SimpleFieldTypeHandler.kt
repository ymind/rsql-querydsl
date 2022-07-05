package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.SimpleExpression
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST")
open class SimpleFieldTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig,
) : FieldTypeHandler {
    override fun supports(type: Class<*>?): Boolean = type != null

    protected fun supportsType(type: Class<*>?, vararg target: Class<*>?): Boolean {
        return if (type == null) false else target.filterNotNull().any { it.isAssignableFrom(type) }
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.path(fieldMetadata.clazz, parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isEmpty()) return null

        return values.map { Expressions.asSimple(it) }.toList()
    }

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = path as SimpleExpression<E>
        val right = values.orEmpty().distinct().toTypedArray()
        val op = if (right.size == 1) {
            when (operator) {
                RsqlOperator.`in` -> RsqlOperator.equals
                RsqlOperator.notIn -> RsqlOperator.notEquals
                else -> operator
            }
        } else operator

        return when {
            op.equals(Operator.ISNULL) -> left.isNull
            op.equals(Operator.ISNOTNULL) -> left.isNotNull

            op.equals(Operator.EQUALS) -> Expressions.booleanOperation(Ops.EQ, path, right[0])
            op.equals(Operator.NOTEQUALS) -> Expressions.booleanOperation(Ops.NE, path, right[0])

            op.equals(Operator.IN) -> Expressions.booleanOperation(Ops.IN, path, Expressions.list(*right))
            op.equals(Operator.NOTIN) -> Expressions.booleanOperation(Ops.NOT_IN, path, Expressions.list(*right))

            else -> null
        }
    }
}
