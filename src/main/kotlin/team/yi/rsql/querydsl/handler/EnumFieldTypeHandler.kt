package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")
class EnumFieldTypeHandler<E : Enum<E>>(
    override val node: ComparisonNode?,
    override val operator: RsqlOperator?,
    override val fieldMetadata: FieldMetadata,
    override val config: RsqlConfig<E>
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, config) {
    override fun supportsType(type: Class<*>): Boolean {
        return supportsType(
            type,
            Enum::class.java,
            Enum::class.javaPrimitiveType,
            java.lang.Enum::class.java,
            java.lang.Enum::class.javaPrimitiveType
        )
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        val type = fieldMetadata.type as? Class<out E>
        val path = Expressions.enumPath(type, parentPath as Path<*>?, fieldMetadata.fieldSelector)

        return path
    }

    override fun getValue(values: List<String?>?, rootPath: Path<*>?, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        return values
            .map { if (it.isNullOrBlank()) null else Expressions.asEnum(java.lang.Enum.valueOf(fieldMetadata.type as Class<E>, it)) }
            .toList()
    }
}
