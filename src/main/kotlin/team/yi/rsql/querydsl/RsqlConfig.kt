package team.yi.rsql.querydsl

import cz.jirutka.rsql.parser.ast.*
import team.yi.rsql.querydsl.exception.*
import team.yi.rsql.querydsl.handler.*
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil
import javax.persistence.EntityManager

class RsqlConfig<E> private constructor(builder: Builder<E>) {
    val fieldTypeHandlers: List<Class<out FieldTypeHandler<*>>>
    val sortFieldTypeHandlers: List<Class<out SortFieldTypeHandler<*>>>

    val entityManager: EntityManager = builder.entityManager
    val operators: List<RsqlOperator> = builder.operators
    val nodesFactory: NodesFactory = NodesFactory(RsqlUtil.getOperators(operators))
    val nodeInterceptors: MutableList<RsqlNodeInterceptor> = builder.nodeInterceptors
    val dateFormat: String? = builder.dateFormat

    init {
        fieldTypeHandlers = RsqlConstants.defaultFieldTypeHandlers + builder.fieldTypeHandlers
        sortFieldTypeHandlers = RsqlConstants.defaultSortFieldTypeHandlers + builder.sortFieldTypeHandlers
    }

    @Suppress("UNCHECKED_CAST")
    fun getFieldTypeHandler(node: ComparisonNode, fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        return getFieldTypeHandler(fieldMetadata.clazz, node, RsqlOperator(node.operator.symbol), fieldMetadata)
    }

    @Suppress("UNCHECKED_CAST")
    fun getFieldTypeHandler(type: Class<*>?, node: ComparisonNode, operator: RsqlOperator, fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        fieldTypeHandlers.forEach { typeHandler ->
            val handler = typeHandler.getDeclaredConstructor(
                ComparisonNode::class.java,
                RsqlOperator::class.java,
                FieldMetadata::class.java,
                RsqlConfig::class.java
            ).newInstance(node, operator, fieldMetadata, this)

            if (handler.supports(type)) return handler as FieldTypeHandler<E>
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    @Suppress("UNCHECKED_CAST")
    fun getSortFieldTypeHandler(fieldMetadata: FieldMetadata): SortFieldTypeHandler<E> {
        val type = fieldMetadata.clazz

        sortFieldTypeHandlers.forEach { typeHandler ->
            val handler = typeHandler.getDeclaredConstructor(
                FieldMetadata::class.java,
            ).newInstance(fieldMetadata)

            if (handler.supports(type)) return handler as SortFieldTypeHandler<E>
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    class Builder<E>(var entityManager: EntityManager) {
        internal var operators = mutableListOf<RsqlOperator>()
        internal var fieldTypeHandlers = mutableListOf<Class<out FieldTypeHandler<*>>>()
        internal var sortFieldTypeHandlers = mutableListOf<Class<out SortFieldTypeHandler<*>>>()
        internal val nodeInterceptors = mutableListOf<RsqlNodeInterceptor>()
        internal var dateFormat: String? = null

        fun operator(vararg operator: RsqlOperator): Builder<E> = this.apply { this.operators += operator }

        fun fieldTypeHandler(vararg typeHandler: Class<out FieldTypeHandler<*>>): Builder<E> = this.apply { this.fieldTypeHandlers += typeHandler }

        fun sortFieldTypeHandler(vararg typeHandler: Class<out SortFieldTypeHandler<*>>): Builder<E> = this.apply { this.sortFieldTypeHandlers += typeHandler }

        @Suppress("UNCHECKED_CAST")
        fun javaFieldTypeHandler(vararg typeHandler: Class<*>): Builder<E> = this.apply {
            this.fieldTypeHandlers += typeHandler.mapNotNull { it as? Class<out FieldTypeHandler<*>> }
        }

        @Suppress("UNCHECKED_CAST")
        fun javaSortFieldTypeHandler(vararg typeHandler: Class<*>): Builder<E> = this.apply {
            this.sortFieldTypeHandlers += typeHandler.mapNotNull { it as? Class<out SortFieldTypeHandler<*>> }
        }

        fun nodeInterceptors(nodeInterceptors: List<RsqlNodeInterceptor>?): Builder<E> = this.apply { nodeInterceptors?.let { this.nodeInterceptors.addAll(nodeInterceptors) } }

        fun nodeInterceptor(block: () -> RsqlNodeInterceptor?): Builder<E> = this.apply { block()?.let { this.nodeInterceptor(it) } }

        @Suppress("MemberVisibilityCanBePrivate")
        fun nodeInterceptor(nodeInterceptor: RsqlNodeInterceptor?): Builder<E> = this.apply { nodeInterceptor?.let { this.nodeInterceptors.add(nodeInterceptor) } }

        fun dateFormat(dateFormat: String?): Builder<E> = this.apply { this.dateFormat = dateFormat }

        @Throws(RsqlException::class)
        fun build(): RsqlConfig<E> {
            return try {
                RsqlConfig(this)
            } catch (ex: Exception) {
                throw RsqlException(ex)
            }
        }
    }
}
