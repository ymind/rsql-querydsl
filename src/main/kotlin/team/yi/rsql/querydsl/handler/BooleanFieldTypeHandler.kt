package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")
class BooleanFieldTypeHandler(
    override val node: ComparisonNode?,
    override val operator: RsqlOperator?,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<Boolean>,
) : ComparableFieldTypeHandler<Boolean>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supportsType(type: Class<*>): Boolean {
        return supportsType(
            type,
            Boolean::class.java,
            Boolean::class.javaPrimitiveType,
            java.lang.Boolean::class.java,
            java.lang.Boolean::class.javaPrimitiveType
        )
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.booleanPath(parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>?, rootPath: Path<*>?, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        return when {
            values.isNullOrEmpty() -> null
            else ->
                values
                    .map { if (it.isNullOrBlank()) null else Expressions.asBoolean(it.toBoolean()) }
                    .toList()
        }
    }

    override fun toComparable(value: String?): Comparable<Boolean>? {
        return value?.toBoolean()
    }

    override fun getExpression(path: Expression<*>, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val operator = this.operator ?: return null
        val left = path as BooleanExpression
        val right = values.orEmpty().map { it as BooleanExpression? }

        return when {
            operator.equals(Operator.ISTRUE) -> left.isTrue
            operator.equals(Operator.ISFALSE) -> left.isFalse
            else -> super.getExpression(left, right, fm ?: fieldMetadata)
        }
    }
}
