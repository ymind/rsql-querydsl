package team.yi.rsql.querydsl.handler

import com.querydsl.core.types.CollectionExpression
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator
import java.math.BigDecimal
import java.util.*

@Suppress("UNCHECKED_CAST")
class FunctionTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : FieldTypeHandler<E> {
    override fun supports(type: Class<*>?): Boolean {
        // "filter": "f{JSON_EXTRACT`s`hashes`$.md5}==9be4f0a737bcb851e8c379a063c27004"
        // "filter": "f{JSON_EXTRACT`s`hashes`$.md5}==f{JSON_EXTRACT`ni`exif`$.XMP[0].tagType}"
        // "filter": "f{JSON_EXTRACT`ni`exif`$.XMP[0}.tagType]>0"
        // "filter": "f{length`ni`hashes}>0"
        return Regex("^f\\{.+}\$").matches(node.selector) && node.arguments.isNotEmpty()
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        val declares = node.selector.substring(1).trim('{', '}').split('`')

        if (declares.size < 2) return null

        val func = declares[0]
        val funcResultType = when {
            declares[1] == "b" -> Boolean::class.java
            declares[1] == "e" -> Enum::class.java
            declares[1] == "s" -> String::class.java
            declares[1] == "nb" -> Byte::class.java
            declares[1] == "ns" -> Short::class.java
            declares[1] == "ni" -> Int::class.java
            declares[1] == "nl" -> Long::class.java
            declares[1] == "nf" -> Float::class.java
            declares[1] == "nd" -> Double::class.java
            declares[1] == "bm" -> BigDecimal::class.java
            declares[1] == "d" -> Date::class.java
            declares[1] == "dt" -> Date::class.java
            declares[1] == "t" -> java.sql.Time::class.java
            else -> fieldMetadata.type
        }
        val fieldPath = Expressions.path(fieldMetadata.type, parentPath as Path<*>?, declares[2])
        val args = mutableListOf<Any>(fieldPath)
        args.addAll(declares.subList(3, declares.size))

        val tplBuilder = StringBuilder("$func(")

        args.forEachIndexed { index, _ ->
            if (index > 0) tplBuilder.append(", ")

            tplBuilder.append("{").append(index).append("}")
        }

        tplBuilder.append(")")

        return when {
            declares[1] == "b" -> Expressions.booleanTemplate(tplBuilder.toString(), *args.toTypedArray())

            declares[1] == "e" -> Expressions.enumTemplate(fieldPath.type as Class<Nothing>, tplBuilder.toString(), *args.toTypedArray())

            declares[1] == "s" -> Expressions.stringTemplate(tplBuilder.toString(), *args.toTypedArray())

            declares[1] == "nb" -> Expressions.numberTemplate(Byte::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "ns" -> Expressions.numberTemplate(Short::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "ni" -> Expressions.numberTemplate(Int::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "nl" -> Expressions.numberTemplate(Long::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "nf" -> Expressions.numberTemplate(Float::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "nd" -> Expressions.numberTemplate(Double::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "nm" -> Expressions.numberTemplate(BigDecimal::class.java, tplBuilder.toString(), *args.toTypedArray())

            declares[1] == "d" -> Expressions.dateTemplate(Date::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "dt" -> Expressions.dateTimeTemplate(Date::class.java, tplBuilder.toString(), *args.toTypedArray())
            declares[1] == "t" -> Expressions.timeTemplate(java.sql.Time::class.java, tplBuilder.toString(), *args.toTypedArray())

            else -> Expressions.template(funcResultType, tplBuilder.toString(), *args.toTypedArray())
        }
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        return (node.arguments ?: return null)
            .map {
                if (Regex("^f\\{.+}\$").matches(it.orEmpty())) {
                    val declares = it.orEmpty().substring(1).trim('{', '}').split('`')

                    if (declares.size < 2) return null

                    val func = declares[0]
                    val funcResultType = when {
                        declares[1] == "b" -> Boolean::class.java
                        declares[1] == "e" -> Enum::class.java
                        declares[1] == "s" -> String::class.java
                        declares[1] == "nb" -> Byte::class.java
                        declares[1] == "ns" -> Short::class.java
                        declares[1] == "ni" -> Int::class.java
                        declares[1] == "nl" -> Long::class.java
                        declares[1] == "nf" -> Float::class.java
                        declares[1] == "nd" -> Double::class.java
                        declares[1] == "bm" -> BigDecimal::class.java
                        declares[1] == "d" -> Date::class.java
                        declares[1] == "dt" -> Date::class.java
                        declares[1] == "t" -> java.sql.Time::class.java
                        else -> fieldMetadata.type
                    }
                    val fieldPath = Expressions.path(funcResultType, rootPath, declares[2])
                    val args = mutableListOf<Any>(fieldPath)
                    args.addAll(declares.subList(3, declares.size))

                    val tplBuilder = StringBuilder("$func(")

                    args.forEachIndexed { index, _ ->
                        if (index > 0) tplBuilder.append(", ")

                        tplBuilder.append("{").append(index).append("}")
                    }

                    tplBuilder.append(")")

                    when {
                        declares[1] == "b" -> Expressions.booleanTemplate(tplBuilder.toString(), *args.toTypedArray())

                        declares[1] == "e" -> Expressions.enumTemplate(fieldPath.type as Class<Nothing>, tplBuilder.toString(), *args.toTypedArray())

                        declares[1] == "s" -> Expressions.stringTemplate(tplBuilder.toString(), *args.toTypedArray())

                        declares[1] == "nb" -> Expressions.numberTemplate(Byte::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "ns" -> Expressions.numberTemplate(Short::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "ni" -> Expressions.numberTemplate(Int::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "nl" -> Expressions.numberTemplate(Long::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "nf" -> Expressions.numberTemplate(Float::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "nd" -> Expressions.numberTemplate(Double::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "nm" -> Expressions.numberTemplate(BigDecimal::class.java, tplBuilder.toString(), *args.toTypedArray())

                        declares[1] == "d" -> Expressions.dateTemplate(Date::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "dt" -> Expressions.dateTimeTemplate(Date::class.java, tplBuilder.toString(), *args.toTypedArray())
                        declares[1] == "t" -> Expressions.timeTemplate(java.sql.Time::class.java, tplBuilder.toString(), *args.toTypedArray())

                        else -> Expressions.template(funcResultType, tplBuilder.toString(), *args.toTypedArray())
                    }
                } else {
                    Expressions.template(fieldMetadata.type, it)
                }
            }
    }

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val arr = values.orEmpty().distinct().map { it as Expression<E> }.toTypedArray()
        val op = if (arr.size == 1) {
            when (operator) {
                RsqlOperator.`in` -> RsqlOperator.equals
                RsqlOperator.notIn -> RsqlOperator.notEquals
                else -> operator
            }
        } else operator

        when {
            op.equals(Operator.ISNULL) -> return (path as SimpleExpression).isNull
            op.equals(Operator.ISNOTNULL) -> return (path as SimpleExpression).isNotNull
            op.equals(Operator.IN) -> return (path as SimpleExpression).`in`(arr as CollectionExpression<out Collection<Nothing>, out Nothing>)
            op.equals(Operator.NOTIN) -> return (path as SimpleExpression).notIn(arr as CollectionExpression<out Collection<Nothing>, out Nothing>)

            op.equals(Operator.EQUALS) -> return (path as SimpleExpression).eq(arr[0] as Expression<Any?>)
            op.equals(Operator.NOTEQUALS) -> return (path as SimpleExpression).ne(arr[0] as Expression<Any?>)

            op.equals(Operator.ISTRUE) -> return (path as BooleanExpression).isTrue
            op.equals(Operator.ISFALSE) -> return (path as BooleanExpression).isFalse

            op.equals(Operator.ISEMPTY) -> return (path as StringExpression).isEmpty
            op.equals(Operator.ISNOTEMPTY) -> return (path as StringExpression).isNotEmpty
            op.equals(Operator.EQUALS_IGNORECASE) -> return (path as StringExpression).equalsIgnoreCase(arr[0] as StringExpression)
            op.equals(Operator.NOTEQUALS_IGNORECASE) -> return (path as StringExpression).notEqualsIgnoreCase(arr[0] as StringExpression)
            op.equals(Operator.LIKE) -> return (path as StringExpression).like(arr[0] as StringExpression)
            op.equals(Operator.NOTLIKE) -> return (path as StringExpression).notLike(arr[0] as StringExpression)
            op.equals(Operator.LIKE_IGNORECASE) -> return (path as StringExpression).likeIgnoreCase(arr[0] as StringExpression)
            op.equals(Operator.STARTWITH) -> return (path as StringExpression).startsWith(arr[0] as StringExpression)
            op.equals(Operator.STARTWITH_IGNORECASE) -> return (path as StringExpression).startsWithIgnoreCase(arr[0] as StringExpression)
            op.equals(Operator.ENDWITH) -> return (path as StringExpression).endsWith(arr[0] as StringExpression)
            op.equals(Operator.ENDWITH_IGNORECASE) -> return (path as StringExpression).endsWithIgnoreCase(arr[0] as StringExpression)
            op.equals(Operator.CONTAINS) -> return (path as StringExpression).contains(arr[0] as StringExpression)
            op.equals(Operator.CONTAINS_IGNORECASE) -> return (path as StringExpression).containsIgnoreCase(arr[0] as StringExpression)

            // number
            op.equals(Operator.GREATER) -> return (path as NumberExpression<Nothing>).gt(arr[0] as Expression<Nothing>)
            op.equals(Operator.GREATER_OR_EQUALS) -> return (path as NumberExpression<Nothing>).goe(arr[0] as Expression<Nothing>)
            op.equals(Operator.LESS_THAN) -> return (path as NumberExpression<Nothing>).lt(arr[0] as Expression<Nothing>)
            op.equals(Operator.LESS_THAN_OR_EQUALS) -> return (path as NumberExpression<Nothing>).loe(arr[0] as Expression<Nothing>)

            op.equals(Operator.BEFORE) -> return (path as TemporalExpression<Comparable<Nothing>>).before(arr[0] as Expression<Comparable<Nothing>>)
            op.equals(Operator.AFTER) -> return (path as TemporalExpression<Comparable<Nothing>>).after(arr[0] as Expression<Comparable<Nothing>>)

            else -> return (path as SimpleTemplate<Any?>).eq(values)
        }
    }
}
