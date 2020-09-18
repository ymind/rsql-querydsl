package team.yi.rsql.querydsl.util

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.PathBuilder
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator
import java.util.*
import javax.persistence.EntityManager

@Suppress("MemberVisibilityCanBePrivate")
object RsqlUtil {
    fun convertStringFieldsToPath(type: Class<*>, fields: List<String>): List<Path<*>> {
        val targetPath = PathBuilder(type, type.simpleName.toLowerCase())

        return fields.mapTo(mutableListOf()) { targetPath[it] }
    }

    fun getClassForEntityString(entityName: String, entityManager: EntityManager): Class<*>? {
        return entityManager.metamodel.entities
            .firstOrNull { entityName == it.name }
            ?.bindableJavaType
    }

    fun convertPathToExpression(paths: Collection<Path<*>>?): Array<Expression<*>> {
        return when {
            paths.isNullOrEmpty() -> emptyArray()
            else -> ArrayList<Expression<*>>(paths).toTypedArray()
        }
    }

    fun getOperators(customOperators: List<RsqlOperator>?): Set<ComparisonOperator> {
        val operators: MutableSet<ComparisonOperator> = Operator.lookup.keys
            .asSequence()
            .map { ComparisonOperator(it, true) }
            .toMutableSet()

        if (customOperators.isNullOrEmpty()) return operators

        customOperators.forEach { operator ->
            operator.symbols.mapTo(operators) { ComparisonOperator(it, true) }
        }

        return operators
    }

    fun validateOperators(operators: List<RsqlOperator>) {
        operators.forEach { operator ->
            operator.symbols.forEach {
                try {
                    ComparisonOperator(it, true)
                } catch (ex: IllegalArgumentException) {
                    throw IllegalArgumentException("Invalid operator symbol: '$it' Operator ${ex.message}")
                }
            }
        }
    }

    fun parseSelectExpression(selectString: String?): List<String>? {
        if (selectString.isNullOrBlank()) return null

        var str = selectString.replace("\\s+".toRegex(), "")

        if (str.startsWith("(")) str = str.substring(1, str.length - 1)

        return listOf(*str.split(",".toRegex()).toTypedArray())
    }

    fun parseSelect(selectString: String?, entityClass: Class<*>): List<Path<*>>? {
        val selectFields = parseSelectExpression(selectString) ?: return null

        return convertStringFieldsToPath(entityClass, selectFields)
    }

    fun parseTwoParamExpression(limit: String?): List<Int> {
        val limitParams = parseSelectExpression(limit)

        require(!(limitParams == null || limitParams.isEmpty() || limitParams.size > 2)) { "Invalid expression" }

        return limitParams.mapTo(mutableListOf()) { it.toInt() }
    }

    fun parseFieldSelector(rootClass: Class<*>, fieldSelector: String?): List<FieldMetadata> {
        var field = fieldSelector

        field?.let {
            if (it.startsWith("f{")) {
                val declares = it.substring(1).trim('{', '}').split('`')

                field = declares[2]
            }
        }

        val nestedFields = field?.split(".").orEmpty()
        val fieldMetadataList = mutableListOf<FieldMetadata>()

        for (i in nestedFields.indices) {
            val fieldMetadata: FieldMetadata = if (i == 0) {
                FieldMetadata(nestedFields[i], rootClass)
            } else {
                FieldMetadata(nestedFields[i], fieldMetadataList[i - 1])
            }

            fieldMetadataList.add(fieldMetadata)
        }

        return fieldMetadataList
    }

    fun parseSortExpression(sort: String?): Map<String, Order> {
        val result: MutableMap<String, Order> = HashMap()
        val sortParams = parseSelectExpression(sort)

        require(!(sortParams == null || sortParams.isEmpty())) { "Invalid expression" }

        for (param in sortParams) {
            val dotLastIndex = param.lastIndexOfAny(charArrayOf('-', '.'))

            require(dotLastIndex != -1) { "Invalid expression" }

            val params = arrayOf(param.substring(0, dotLastIndex), param.substring(dotLastIndex + 1))

            result[params[0]] = Order.valueOf(value = params[1].toUpperCase())
        }

        return result
    }
}
