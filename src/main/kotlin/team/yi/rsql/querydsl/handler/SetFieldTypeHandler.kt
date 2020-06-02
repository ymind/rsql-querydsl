package team.yi.rsql.querydsl.handler

import com.querydsl.core.alias.DefaultTypeSystem
import com.querydsl.core.alias.TypeSystem
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")
class SetFieldTypeHandler<E>(
    override val node: ComparisonNode?,
    override val operator: RsqlOperator?,
    override val fieldMetadata: FieldMetadata,
    override val config: RsqlConfig<E>
) : CollectionFieldTypeHandler<E>(node, operator, fieldMetadata, config) {
    override fun supportsType(type: Class<*>): Boolean {
        val typeSystem: TypeSystem = DefaultTypeSystem()

        return typeSystem.isSetType(type)
    }
}
