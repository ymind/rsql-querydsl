package team.yi.rsql.querydsl

import com.mysema.codegen.model.TypeCategory
import com.querydsl.codegen.EntityType
import com.querydsl.codegen.TypeFactory
import com.querydsl.core.types.dsl.*
import team.yi.rsql.querydsl.exception.FieldNotSupportedException
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

@Suppress("MemberVisibilityCanBePrivate")
class FieldMetadata {
    val type: Class<*>
    var fieldSelector: String? = null
        private set
    var fieldSelectorIndex: Int? = null
        private set
    var field: Field? = null
        private set
    var parameterizedType: Class<*>? = null
        private set
    var collection: Boolean? = null
        private set
    var entityType: EntityType? = null
        private set
    var pathType: Class<*>? = null
        private set
    var parent: FieldMetadata? = null
        private set
    val collectionType: Class<*>
        get() = parameterizedType ?: type

    constructor(type: Class<*>, parent: FieldMetadata?) {
        this.type = type
        this.parent = parent
    }

    constructor(fieldSelector: String, parent: FieldMetadata) {
        this.fieldSelector = fieldSelector
        this.fieldSelectorIndex = parseFieldSelector(fieldSelector)
        this.parent = parent
        this.field = getField(parent, fieldSelector)
        this.type = getClass(field)
        this.entityType = getEntityType(parameterizedType ?: type)
        this.pathType = getPathType(entityType)
    }

    constructor(fieldSelector: String, rootClass: Class<*>) {
        this.fieldSelector = fieldSelector
        this.fieldSelectorIndex = parseFieldSelector(fieldSelector)
        this.field = getField(rootClass, fieldSelector, this)
        this.type = getClass(field)
        this.entityType = getEntityType(parameterizedType ?: type)
        this.pathType = getPathType(entityType)
    }

    private fun parseFieldSelector(fieldSelector: String): Int? {
        return getListIndex(fieldSelector)?.also { this.fieldSelector = removeListIndex(fieldSelector) }
    }

    private fun getClass(field: Field?): Class<*> {
        if (List::class.java.isAssignableFrom(field!!.type) || Set::class.java.isAssignableFrom(field.type)) {
            this.collection = true

            val listType = field.genericType as ParameterizedType

            this.parameterizedType = listType.actualTypeArguments[0] as Class<*>
        }

        return field.type
    }

    companion object {
        fun getEntityType(entityClass: Class<*>): EntityType = TypeFactory().getEntityType(entityClass)

        fun getPathType(entityType: EntityType?): Class<*> {
            return when (entityType?.originalCategory) {
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

        fun getField(fieldMetadata: FieldMetadata, fieldName: String): Field {
            val clazz: Class<out Any> = fieldMetadata.parameterizedType ?: fieldMetadata.type

            return this.getField(clazz, fieldName, fieldMetadata)
        }

        fun getField(clazz: Class<*>, fieldName: String, fieldMetadata: FieldMetadata): Field {
            return try {
                clazz.getDeclaredField(fieldName)
            } catch (nsf: NoSuchFieldException) {
                if (clazz.superclass != null) return getField(clazz.superclass, fieldName, fieldMetadata)

                val message = "Invalid where clause: '${fieldMetadata.fieldSelector}' Could not locate field '$fieldName' on class ${fieldMetadata.type}"

                throw FieldNotSupportedException(message, fieldMetadata.type, fieldName, fieldMetadata.fieldSelector)
            }
        }

        private fun getListIndex(fieldSelector: String): Int? {
            val startIndex = fieldSelector.indexOf('[')
            val endIndex = fieldSelector.indexOf(']')

            return if (startIndex == -1 || endIndex == -1) null else fieldSelector.substring(startIndex + 1, endIndex).toInt()
        }

        private fun removeListIndex(fieldSelector: String): String {
            val stringBuilder = StringBuilder(fieldSelector)
            val startIndex = fieldSelector.indexOf('[')
            val endIndex = fieldSelector.indexOf(']')

            return when {
                startIndex == -1 || endIndex == -1 -> fieldSelector
                else -> stringBuilder.replace(startIndex, endIndex + 1, "").toString()
            }
        }
    }
}
