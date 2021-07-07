package team.yi.rsql.querydsl

import cz.jirutka.rsql.parser.ast.*
import team.yi.rsql.querydsl.exception.*
import team.yi.rsql.querydsl.handler.*
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil
import javax.persistence.EntityManager

class RsqlConfig<E> private constructor(builder: Builder<E>) {
    private val fieldTypeHandlers: MutableList<Class<out FieldTypeHandler<*>>>
    private val sortFieldTypeHandlers: MutableList<Class<out SortFieldTypeHandler<*>>>

    @Suppress("MemberVisibilityCanBePrivate")
    val defaultFieldTypeHandlers: List<Class<out FieldTypeHandler<*>>>
        get() = listOf(
            FunctionTypeHandler::class.java,
            NumberFieldTypeHandler::class.java,
            EnumFieldTypeHandler::class.java,
            StringFieldTypeHandler::class.java,
            CharacterFieldTypeHandler::class.java,
            DateTimeFieldTypeHandler::class.java,
            BooleanFieldTypeHandler::class.java,
            ListFieldTypeHandler::class.java,
            SetFieldTypeHandler::class.java,
            CollectionFieldTypeHandler::class.java,
            SimpleFieldTypeHandler::class.java,
        )

    @Suppress("MemberVisibilityCanBePrivate")
    val defaultSortFieldTypeHandlers: List<Class<out SortFieldTypeHandler<*>>>
        get() = listOf(
            DefaultSortFieldTypeHandler::class.java,
        )

    val entityManager: EntityManager = builder.entityManager
    var operators: List<RsqlOperator> = builder.operators.orEmpty()
    val nodesFactory: NodesFactory = NodesFactory(RsqlUtil.getOperators(operators))
    val nodeInterceptors = builder.nodeInterceptors
    var dateFormat: String? = builder.dateFormat

    init {
        fieldTypeHandlers = defaultFieldTypeHandlers.toMutableList()
        sortFieldTypeHandlers = defaultSortFieldTypeHandlers.toMutableList()

        builder.fieldTypeHandlers?.let { fieldTypeHandlers.addAll(it) }
        builder.sortFieldTypeHandlers?.let { sortFieldTypeHandlers.addAll(it) }
    }

    fun addFieldTypeHandler(vararg typeHandler: Class<out FieldTypeHandler<E>>) {
        this.fieldTypeHandlers.addAll(typeHandler)
    }

    fun addSortFieldTypeHandler(vararg typeHandler: Class<out SortFieldTypeHandler<E>>) {
        this.sortFieldTypeHandlers.addAll(typeHandler)
    }

    @Suppress("UNCHECKED_CAST")
    fun getFieldTypeHandler(node: ComparisonNode, fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        return getFieldTypeHandler(fieldMetadata.type, node, RsqlOperator(node.operator.symbol), fieldMetadata)
    }

    @Suppress("UNCHECKED_CAST")
    fun getFieldTypeHandler(type: Class<*>?, node: ComparisonNode, operator: RsqlOperator, fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        for (typeHandler in fieldTypeHandlers) {
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
        val type = fieldMetadata.type

        for (typeHandler in sortFieldTypeHandlers) {
            val handler = typeHandler.getDeclaredConstructor(
                FieldMetadata::class.java,
            ).newInstance(fieldMetadata)

            if (handler.supports(type)) return handler as SortFieldTypeHandler<E>
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    class Builder<E>(var entityManager: EntityManager) {
        internal var operators: List<RsqlOperator>? = null
        internal var fieldTypeHandlers: List<Class<out FieldTypeHandler<*>>>? = null
        internal var sortFieldTypeHandlers: List<Class<out SortFieldTypeHandler<*>>>? = null
        internal val nodeInterceptors = mutableListOf<RsqlNodeInterceptor>()
        internal var dateFormat: String? = null

        fun operator(vararg operator: RsqlOperator): Builder<E> = this.also { this.operators = operator.toList() }
        fun fieldTypeHandler(vararg typeHandler: Class<out FieldTypeHandler<*>>): Builder<E> = this.also { this.fieldTypeHandlers = typeHandler.toList() }
        fun sortFieldTypeHandler(vararg typeHandler: Class<out SortFieldTypeHandler<*>>): Builder<E> = this.also { this.sortFieldTypeHandlers = typeHandler.toList() }

        @Suppress("UNCHECKED_CAST")
        fun javaFieldTypeHandler(vararg typeHandler: Class<*>): Builder<E> = this.also {
            this.fieldTypeHandlers = typeHandler.mapNotNull { it as? Class<out FieldTypeHandler<*>> }
        }

        @Suppress("UNCHECKED_CAST", "unused")
        fun javaSortFieldTypeHandler(vararg typeHandler: Class<*>): Builder<E> = this.also {
            this.sortFieldTypeHandlers = typeHandler.mapNotNull { it as? Class<out SortFieldTypeHandler<*>> }
        }

        fun nodeInterceptors(nodeInterceptors: List<RsqlNodeInterceptor>?): Builder<E> {
            nodeInterceptors?.let { this.nodeInterceptors.addAll(nodeInterceptors) }

            return this
        }

        fun nodeInterceptor(block: () -> RsqlNodeInterceptor?): Builder<E> {
            block()?.let { this.nodeInterceptor(it) }

            return this
        }

        fun nodeInterceptor(nodeInterceptor: RsqlNodeInterceptor?): Builder<E> {
            nodeInterceptor?.let { this.nodeInterceptors.add(nodeInterceptor) }

            return this
        }

        fun dateFormat(dateFormat: String?): Builder<E> = this.also { this.dateFormat = dateFormat }

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
