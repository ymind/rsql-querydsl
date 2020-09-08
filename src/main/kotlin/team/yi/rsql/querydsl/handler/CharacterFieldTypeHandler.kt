package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")
class CharacterFieldTypeHandler<E>(
    override val node: ComparisonNode?,
    override val operator: RsqlOperator?,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supportsType(type: Class<*>): Boolean {
        return supportsType(
            type,
            Char::class.java,
            Char::class.javaPrimitiveType,
            java.lang.Character::class.java,
            java.lang.Character::class.javaPrimitiveType
        )
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.path(fieldMetadata.type, parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>?, rootPath: Path<*>?, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        return values
            .map { if (it.isNullOrBlank()) null else Expressions.asSimple(it[0]) }
            .toList()
    }
}
