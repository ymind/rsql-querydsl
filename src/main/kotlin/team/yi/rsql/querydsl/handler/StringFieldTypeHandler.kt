package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST")
class StringFieldTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
        return supportsType(
            type,
            String::class.java,
            String::class.javaPrimitiveType,
            java.lang.String::class.java,
            java.lang.String::class.javaPrimitiveType
        )
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.stringPath(parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        return when {
            values.isNullOrEmpty() -> null
            else -> values.map { if (it.isNullOrBlank()) null else Expressions.asString(it) }.toList()
        }
    }

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = path as StringExpression
        val right = values.orEmpty().distinct().map { it as Expression<String?>? }

        return when {
            operator.equals(Operator.CONTAINS) -> left.contains(right[0])
            operator.equals(Operator.CONTAINS_IGNORECASE) -> left.containsIgnoreCase(right[0])

            operator.equals(Operator.ENDWITH) -> left.endsWith(right[0])
            operator.equals(Operator.ENDWITH_IGNORECASE) -> left.endsWithIgnoreCase(right[0])

            // operator.equals(Operator.EQUALS) -> left.eq(right[0])
            operator.equals(Operator.EQUALS_IGNORECASE) -> left.equalsIgnoreCase(right[0])
            operator.equals(Operator.NOTEQUALS_IGNORECASE) -> left.notEqualsIgnoreCase(right[0])

            operator.equals(Operator.ISEMPTY) -> left.isEmpty
            operator.equals(Operator.ISNOTEMPTY) -> left.isNotEmpty

            operator.equals(Operator.LIKE) -> left.like(right[0])
            operator.equals(Operator.LIKE_IGNORECASE) -> left.likeIgnoreCase(right[0])
            operator.equals(Operator.NOTLIKE) -> left.notLike(right[0])

            operator.equals(Operator.MATCHES) -> left.matches(right[0])

            operator.equals(Operator.STARTWITH) -> left.startsWith(right[0])
            operator.equals(Operator.STARTWITH_IGNORECASE) -> left.startsWithIgnoreCase(right[0])

            else -> super.getExpression(left, right, fm ?: fieldMetadata)
        }
    }
}
