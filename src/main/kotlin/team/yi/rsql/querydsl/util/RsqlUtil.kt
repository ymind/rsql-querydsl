package team.yi.rsql.querydsl.util

import com.querydsl.codegen.*
import com.querydsl.codegen.utils.model.TypeCategory
import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import jakarta.persistence.EntityManager
import team.yi.rsql.querydsl.operator.*
import team.yi.rsql.querydsl.operator.Operator
import java.lang.reflect.*
import java.util.*

object RsqlUtil {
    fun getEntityType(entityClass: Class<*>?): EntityType? = entityClass?.let { TypeFactory().getEntityType(it) }

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

    fun getParameterizedType(field: Field?): Class<*>? {
        return field?.let {
            if (Collection::class.java.isAssignableFrom(it.type)) {
                val listType = it.genericType as ParameterizedType

                return listType.actualTypeArguments[0] as Class<*>
            }

            return null
        }
    }

    fun getClassForEntityString(entityName: String, entityManager: EntityManager): Class<*>? {
        return entityManager.metamodel.entities
            .firstOrNull { it.name == entityName }
            ?.bindableJavaType
    }

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

    fun validateOperators(operators: List<RsqlOperator>) {
        operators.forEach { operator ->
            operator.symbols.forEach {
                runCatching {
                    ComparisonOperator(it, true)
                }.onFailure { ex ->
                    throw IllegalArgumentException("Invalid operator symbol: '$it' Operator ${ex.message}", ex)
                }
            }
        }
    }

    fun <T> parseSelect(selectString: String, pathBuilder: PathBuilder<T>): List<Path<*>> {
        val selectFields = parseSelectExpression(selectString)

        return if (selectFields.isEmpty()) {
            emptyList()
        } else {
            selectFields.map {
                val field = kotlin.runCatching {
                    pathBuilder.type.getDeclaredField(it)
                }.getOrNull() ?: return@map pathBuilder[it]

                if (field.type == List::class.java) {
                    val parameterizedType = field.genericType as ParameterizedType
                    val actualTypeArgument = parameterizedType.actualTypeArguments.first()

                    return@map pathBuilder.getList(it, actualTypeArgument as Class<*>)
                }

                return@map pathBuilder[it]
            }
        }
    }

    fun parseSelectExpression(selectString: String?): List<String> {
        if (selectString.isNullOrBlank()) return emptyList()

        val str = selectString.replace("\\s+".toRegex(), "").trim('(', ')')

        return str.split(',').distinct()
    }

    fun parseSortExpression(sort: String?): Map<String, Order> {
        val result = mutableMapOf<String, Order>()
        val sortParams = parseSelectExpression(sort)

        require(sortParams.isNotEmpty()) { "Invalid expression" }

        sortParams.forEach { param ->
            val dotLastIndex = param.lastIndexOfAny(charArrayOf('-', '.'))

            require(dotLastIndex != -1) { "Invalid expression" }

            val params = arrayOf(param.substring(0, dotLastIndex), param.substring(dotLastIndex + 1))

            result[params[0]] = Order.valueOf(value = params[1].uppercase(Locale.getDefault()))
        }

        return result
    }
}
