package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.DateUtil

@Suppress("UNCHECKED_CAST")
class DateTimeFieldTypeHandler<E : Comparable<E>>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : TemporalFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.dateTimePath(
            fieldMetadata.clazz as Class<out Comparable<*>?>,
            parentPath as Path<*>?,
            fieldMetadata.fieldSelector
        )
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isEmpty()) return null

        return values.map {
            val value = toComparable(it) ?: return null

            Expressions.asDateTime(value)
        }.toList()
    }

    override fun toComparable(value: String?): Comparable<E>? {
        if (value.isNullOrBlank()) return null

        val dateFormat = rsqlConfig.dateFormat

        return if (dateFormat.isNullOrEmpty()) {
            DateUtil.parse(value)
        } else {
            DateUtil.parse(value, dateFormat)
        } as? Comparable<E>?
    }
}
