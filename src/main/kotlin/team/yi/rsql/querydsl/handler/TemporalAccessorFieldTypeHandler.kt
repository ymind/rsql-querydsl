package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.operator.RsqlOperator
import java.time.*
import java.time.temporal.TemporalAccessor

@Suppress("UNCHECKED_CAST")
class TemporalAccessorFieldTypeHandler<E : Comparable<E>>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig,
) : TemporalFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
        return if (type == null) {
            false
        } else {
            TemporalAccessor::class.java.isAssignableFrom(type)
        }
    }

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
            val value = toComparable(it, fm) ?: return null

            Expressions.asDateTime(value)
        }.toList()
    }

    @Suppress("CyclomaticComplexMethod")
    override fun toComparable(value: String?, fm: FieldMetadata?): Comparable<E>? {
        if (value.isNullOrBlank()) return null

        val fieldType = fm?.clazz ?: return null

        return runCatching {
            when (fieldType) {
                LocalDate::class.java -> LocalDate.parse(value)
                LocalDateTime::class.java -> LocalDateTime.parse(value)
                LocalTime::class.java -> LocalTime.parse(value)
                OffsetDateTime::class.java -> OffsetDateTime.parse(value)
                OffsetTime::class.java -> OffsetTime.parse(value)
                ZonedDateTime::class.java -> ZonedDateTime.parse(value)
                Duration::class.java -> Duration.parse(value)
                Period::class.java -> Period.parse(value)
                Instant::class.java -> Instant.parse(value)
                MonthDay::class.java -> MonthDay.parse(value)
                Year::class.java -> Year.parse(value)
                YearMonth::class.java -> YearMonth.parse(value)
                Month::class.java -> Month.valueOf(value)
                DayOfWeek::class.java -> DayOfWeek.valueOf(value)
                else -> null
            } as? Comparable<E>?
        }.getOrNull()
    }
}
