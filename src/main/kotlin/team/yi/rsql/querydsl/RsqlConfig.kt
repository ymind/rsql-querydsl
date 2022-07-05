package team.yi.rsql.querydsl

import cz.jirutka.rsql.parser.ast.*
import team.yi.rsql.querydsl.exception.*
import team.yi.rsql.querydsl.handler.*
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil
import javax.persistence.EntityManager

class RsqlConfig private constructor(builder: Builder) {
    val fieldTypeHandlers: List<Class<out FieldTypeHandler>>
    val sortFieldTypeHandlers: List<Class<out SortFieldTypeHandler>>

    val entityManager: EntityManager = builder.entityManager
    val operators: List<RsqlOperator> = builder.operators
    val nodesFactory: NodesFactory = NodesFactory(RsqlUtil.getOperators(operators))
    val nodeInterceptors: MutableList<RsqlNodeInterceptor> = builder.nodeInterceptors
    val dateFormat: String? = builder.dateFormat

    init {
        fieldTypeHandlers = RsqlConstants.defaultFieldTypeHandlers + builder.fieldTypeHandlers
        sortFieldTypeHandlers = RsqlConstants.defaultSortFieldTypeHandlers + builder.sortFieldTypeHandlers
    }

    fun getFieldTypeHandler(node: ComparisonNode, fieldMetadata: FieldMetadata): FieldTypeHandler {
        return getFieldTypeHandler(fieldMetadata.clazz, node, RsqlOperator(node.operator.symbol), fieldMetadata)
    }

    fun getFieldTypeHandler(type: Class<*>?, node: ComparisonNode, operator: RsqlOperator, fieldMetadata: FieldMetadata): FieldTypeHandler {
        fieldTypeHandlers.forEach { typeHandler ->
            val handler = typeHandler.getDeclaredConstructor(
                ComparisonNode::class.java,
                RsqlOperator::class.java,
                FieldMetadata::class.java,
                RsqlConfig::class.java
            ).newInstance(node, operator, fieldMetadata, this)

            if (handler.supports(type)) return handler
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    fun getSortFieldTypeHandler(fieldMetadata: FieldMetadata): SortFieldTypeHandler {
        val type = fieldMetadata.clazz

        sortFieldTypeHandlers.forEach { typeHandler ->
            val handler = typeHandler.getDeclaredConstructor(
                FieldMetadata::class.java,
            ).newInstance(fieldMetadata)

            if (handler.supports(type)) return handler
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    class Builder(var entityManager: EntityManager) {
        internal var operators = mutableListOf<RsqlOperator>()
        internal var fieldTypeHandlers = mutableListOf<Class<out FieldTypeHandler>>()
        internal var sortFieldTypeHandlers = mutableListOf<Class<out SortFieldTypeHandler>>()
        internal val nodeInterceptors = mutableListOf<RsqlNodeInterceptor>()
        internal var dateFormat: String? = null

        fun operator(vararg operator: RsqlOperator): Builder = this.apply { this.operators += operator }

        fun fieldTypeHandler(vararg typeHandler: Class<out FieldTypeHandler>): Builder = this.apply { this.fieldTypeHandlers += typeHandler }

        fun sortFieldTypeHandler(vararg typeHandler: Class<out SortFieldTypeHandler>): Builder = this.apply { this.sortFieldTypeHandlers += typeHandler }

        @Suppress("UNCHECKED_CAST")
        fun javaFieldTypeHandler(vararg typeHandler: Class<*>): Builder = this.apply {
            this.fieldTypeHandlers += typeHandler.mapNotNull { it as? Class<out FieldTypeHandler> }
        }

        @Suppress("UNCHECKED_CAST")
        fun javaSortFieldTypeHandler(vararg typeHandler: Class<*>): Builder = this.apply {
            this.sortFieldTypeHandlers += typeHandler.mapNotNull { it as? Class<out SortFieldTypeHandler> }
        }

        fun nodeInterceptors(nodeInterceptors: List<RsqlNodeInterceptor>?): Builder = this.apply { nodeInterceptors?.let { this.nodeInterceptors.addAll(nodeInterceptors) } }

        fun nodeInterceptor(block: () -> RsqlNodeInterceptor?): Builder = this.apply { block()?.let { this.nodeInterceptor(it) } }

        @Suppress("MemberVisibilityCanBePrivate")
        fun nodeInterceptor(nodeInterceptor: RsqlNodeInterceptor?): Builder = this.apply { nodeInterceptor?.let { this.nodeInterceptors.add(nodeInterceptor) } }

        fun dateFormat(dateFormat: String?): Builder = this.apply { this.dateFormat = dateFormat }

        @Throws(RsqlException::class)
        fun build(): RsqlConfig {
            return try {
                RsqlConfig(this)
            } catch (ex: Exception) {
                throw RsqlException(ex)
            }
        }
    }
}
