package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.core.util.MathUtils
import cz.jirutka.rsql.parser.ast.ComparisonNode
import org.apache.commons.lang3.math.NumberUtils
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator
import java.math.BigDecimal
import java.math.BigInteger

@Suppress("UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")
class NumberFieldTypeHandler<E>(
    override val node: ComparisonNode?,
    override val operator: RsqlOperator?,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) where E : Number, E : Comparable<E> {
    override fun supportsType(type: Class<*>): Boolean {
        return supportsType(
            type,
            Byte::class.java, Byte::class.javaPrimitiveType, java.lang.Byte::class.java, java.lang.Byte::class.javaPrimitiveType,
            Short::class.java, Short::class.javaPrimitiveType, java.lang.Short::class.java, java.lang.Short::class.javaPrimitiveType,
            Int::class.java, Int::class.javaPrimitiveType, java.lang.Integer::class.java, java.lang.Integer::class.javaPrimitiveType,
            Long::class.java, Long::class.javaPrimitiveType, java.lang.Long::class.java, java.lang.Long::class.javaPrimitiveType,
            Float::class.java, Float::class.javaPrimitiveType, java.lang.Float::class.java, java.lang.Float::class.javaPrimitiveType,
            Double::class.java, Double::class.javaPrimitiveType, java.lang.Double::class.java, java.lang.Double::class.javaPrimitiveType,
            BigInteger::class.java, BigInteger::class.javaPrimitiveType,
            BigDecimal::class.java, BigDecimal::class.javaPrimitiveType,
            Number::class.java, Number::class.javaPrimitiveType, java.lang.Number::class.java, java.lang.Number::class.javaPrimitiveType
        )
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return Expressions.numberPath(fieldMetadata.type as Class<E>, parentPath as Path<*>?, fieldMetadata.fieldSelector)
    }

    override fun getValue(values: List<String?>?, rootPath: Path<*>?, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        return values.map {
            val value = toNumber(it) ?: return null

            Expressions.asNumber(value)
        }.toList()
    }

    @Suppress("ProtectedInFinal")
    protected fun toNumber(value: String?): E? {
        if (value.isNullOrBlank()) return null

        val number = NumberUtils.createNumber(value)

        return MathUtils.cast(number, fieldMetadata.type as Class<out E?>)
    }

    override fun getExpression(path: Expression<*>, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val operator = this.operator ?: return null
        val left = path as NumberExpression<E>
        val right = values.orEmpty().map { it as NumberExpression<E> }

        return when {
            operator.equals(Operator.BETWEEN) -> left.between(right[0], right[1])
            operator.equals(Operator.NOTBETWEEN) -> left.notBetween(right[0], right[1])

            operator.equals(Operator.GREATER) -> left.gt(right[0])
            operator.equals(Operator.GREATER_OR_EQUALS) -> left.goe(right[0])

            operator.equals(Operator.LESS_THAN) -> left.lt(right[0])
            operator.equals(Operator.LESS_THAN_OR_EQUALS) -> left.loe(right[0])

            else -> super.getExpression(left, right, fm ?: fieldMetadata)
        }
    }
}
