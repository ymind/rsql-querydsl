package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.operator.*
import team.yi.rsql.querydsl.operator.Operator

class BooleanFieldTypeHandler(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig,
) : ComparableFieldTypeHandler<Boolean>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
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

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        return when {
            values.isEmpty() -> null
            else -> values.map { if (it.isNullOrBlank()) null else Expressions.asBoolean(it.toBoolean()) }.toList()
        }
    }

    override fun toComparable(value: String?): Comparable<Boolean>? {
        return value?.toBoolean()
    }

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = path as BooleanExpression
        val right = values.orEmpty().distinct().map { it as BooleanExpression? }

        return when {
            operator.equals(Operator.ISTRUE) -> left.isTrue
            operator.equals(Operator.ISFALSE) -> left.isFalse
            else -> super.getExpression(left, right, fm ?: fieldMetadata)
        }
    }
}
