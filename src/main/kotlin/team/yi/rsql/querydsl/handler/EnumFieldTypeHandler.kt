package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST")
class EnumFieldTypeHandler<E : Enum<E>>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
        return supportsType(
            type,
            Enum::class.java,
            Enum::class.javaPrimitiveType,
            java.lang.Enum::class.java,
            java.lang.Enum::class.javaPrimitiveType
        )
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        val type = fieldMetadata.clazz as? Class<out E>

        return Expressions.enumPath(type, parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isEmpty()) return null

        return values
            .map { if (it.isNullOrBlank()) null else Expressions.asEnum(java.lang.Enum.valueOf(fieldMetadata.clazz as Class<E>, it)) }
            .toList()
    }
}
