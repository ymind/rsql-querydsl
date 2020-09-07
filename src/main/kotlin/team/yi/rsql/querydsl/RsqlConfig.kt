package team.yi.rsql.querydsl

import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.exception.RsqlException
import team.yi.rsql.querydsl.exception.TypeNotSupportedException
import team.yi.rsql.querydsl.handler.*
import team.yi.rsql.querydsl.operator.RsqlOperator
import javax.persistence.EntityManager

class RsqlConfig<E> private constructor(builder: Builder<E>) {
    private val fieldTypeHandlers: MutableList<Class<out FieldTypeHandler<*>>>

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

    val entityManager: EntityManager
    var operators: List<RsqlOperator>
    var dateFormat: String

    init {
        this.entityManager = builder.entityManager
        this.operators = builder.operators.orEmpty()
        this.dateFormat = builder.dateFormat.orEmpty()

        this.fieldTypeHandlers = mutableListOf()
        this.fieldTypeHandlers.addAll(defaultFieldTypeHandlers)

        builder.fieldTypeHandlers?.let { fieldTypeHandlers.addAll(it) }
    }

    fun addFieldTypeHandlers(fieldTypeHandlers: List<Class<out FieldTypeHandler<E>>>?) {
        fieldTypeHandlers?.let { this.fieldTypeHandlers.addAll(fieldTypeHandlers) }
    }

    @Suppress("UNCHECKED_CAST")
    fun getFieldTypeHandler(node: ComparisonNode?, operator: RsqlOperator?, fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        return getFieldTypeHandler(fieldMetadata.type, node, operator, fieldMetadata)
    }

    @Suppress("UNCHECKED_CAST")
    fun getFieldTypeHandler(type: Class<*>, node: ComparisonNode?, operator: RsqlOperator?, fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        for (fieldType in fieldTypeHandlers) {
            val handler = fieldType.getDeclaredConstructor(
                ComparisonNode::class.java,
                RsqlOperator::class.java,
                FieldMetadata::class.java,
                RsqlConfig::class.java
            ).newInstance(node, operator, fieldMetadata, this)

            if (handler.supportsType(type)) return handler as FieldTypeHandler<E>
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    @Suppress("UNCHECKED_CAST")
    fun getSortFieldTypeHandler(fieldMetadata: FieldMetadata): FieldTypeHandler<E> {
        val type = fieldMetadata.type

        for (fieldType in fieldTypeHandlers) {
            if (!SortFieldTypeHandler::class.java.isAssignableFrom(fieldType)) continue

            val handler = fieldType.getDeclaredConstructor(
                ComparisonNode::class.java,
                RsqlOperator::class.java,
                FieldMetadata::class.java,
                RsqlConfig::class.java
            )
                .newInstance(null, null, fieldMetadata, this)

            if (handler.supportsType(type)) return handler as FieldTypeHandler<E>
        }

        throw TypeNotSupportedException("Type is not supported: $type")
    }

    @Suppress("unused")
    class Builder<E>(var entityManager: EntityManager) {
        var operators: List<RsqlOperator>? = null
        var fieldTypeHandlers: List<Class<out FieldTypeHandler<*>>>? = null
        var dateFormat: String? = null

        fun operators(operators: MutableList<RsqlOperator>?): Builder<E> = this.also { this.operators = operators }
        fun operator(operator: RsqlOperator): Builder<E> = this.also { this.operators = listOf(operator) }
        fun fieldTypeHandlers(fieldTypeHandlers: MutableList<Class<out FieldTypeHandler<*>>>?): Builder<E> = this.also { this.fieldTypeHandlers = fieldTypeHandlers }
        fun fieldTypeHandler(fieldTypeHandler: Class<out FieldTypeHandler<*>>): Builder<E> = this.also { this.fieldTypeHandlers = listOf(fieldTypeHandler) }

        @Suppress("UNCHECKED_CAST")
        fun javaFieldTypeHandler(fieldTypeHandler: Class<*>): Builder<E> = this.also {
            this.fieldTypeHandlers = listOfNotNull(fieldTypeHandler as? Class<out FieldTypeHandler<*>>)
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
