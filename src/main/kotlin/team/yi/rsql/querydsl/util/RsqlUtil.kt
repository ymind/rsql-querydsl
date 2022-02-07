package team.yi.rsql.querydsl.util

import com.querydsl.codegen.EntityType
import com.querydsl.codegen.TypeFactory
import com.querydsl.codegen.utils.model.TypeCategory
import com.querydsl.core.types.Order
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import team.yi.rsql.querydsl.operator.Operator
import team.yi.rsql.querydsl.operator.RsqlOperator
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.persistence.EntityManager

@Suppress("MemberVisibilityCanBePrivate")
object RsqlUtil {
    @JvmStatic
    fun getEntityType(entityClass: Class<*>?): EntityType? = entityClass?.let { TypeFactory().getEntityType(it) }

    @JvmStatic
    fun getPathType(entityType: EntityType?): Class<*>? {
        if (entityType == null) return null

        return when (entityType.originalCategory) {
            TypeCategory.COMPARABLE -> ComparablePath::class.java
            TypeCategory.ENUM -> EnumPath::class.java
            TypeCategory.DATE -> DatePath::class.java
            TypeCategory.DATETIME -> DateTimePath::class.java
            TypeCategory.TIME -> TimePath::class.java
            TypeCategory.NUMERIC -> NumberPath::class.java
            TypeCategory.STRING -> StringPath::class.java
            TypeCategory.BOOLEAN -> BooleanPath::class.java
            else -> EntityPathBase::class.java
        }
    }

    @JvmStatic
    fun getParameterizedType(field: Field?): Class<*>? {
        return field?.let {
            if (Collection::class.java.isAssignableFrom(it.type)) {
                val listType = it.genericType as ParameterizedType

                return listType.actualTypeArguments[0] as Class<*>
            }

            return null
        }
    }

    @JvmStatic
    fun getClassForEntityString(entityName: String, entityManager: EntityManager): Class<*>? {
        return entityManager.metamodel.entities
            .firstOrNull { it.name == entityName }
            ?.bindableJavaType
    }

    @JvmStatic
    fun getOperators(customOperators: List<RsqlOperator>?): Set<ComparisonOperator> {
        val operators = Operator.lookup.keys
            .asSequence()
            .map { ComparisonOperator(it, true) }
            .toMutableSet()

        if (customOperators.isNullOrEmpty()) return operators

        customOperators.forEach { operator ->
            operator.symbols.mapTo(operators) {
                ComparisonOperator(it, true)
            }
        }

        return operators
    }

    @JvmStatic
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

    @JvmStatic
    fun <T> parseSelect(selectString: String, pathBuilder: PathBuilder<T>): List<Path<*>> {
        val selectFields = parseSelectExpression(selectString)

        return if (selectFields.isEmpty()) emptyList() else selectFields.map { pathBuilder[it] }
    }

    @JvmStatic
    fun parseSelectExpression(selectString: String?): List<String> {
        if (selectString.isNullOrBlank()) return emptyList()

        val str = selectString.replace("\\s+".toRegex(), "").trim('(', ')')

        return str.split(',').distinct()
    }

    @JvmStatic
    fun parseSortExpression(sort: String?): Map<String, Order> {
        val result: MutableMap<String, Order> = HashMap()
        val sortParams = parseSelectExpression(sort)

        require(sortParams.isNotEmpty()) { "Invalid expression" }

        for (param in sortParams) {
            val dotLastIndex = param.lastIndexOfAny(charArrayOf('-', '.'))

            require(dotLastIndex != -1) { "Invalid expression" }

            val params = arrayOf(param.substring(0, dotLastIndex), param.substring(dotLastIndex + 1))

            result[params[0]] = Order.valueOf(value = params[1].uppercase(Locale.getDefault()))
        }

        return result
    }
}
