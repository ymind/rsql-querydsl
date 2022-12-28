package team.yi.rsql.querydsl

import com.querydsl.codegen.EntityType
import team.yi.rsql.querydsl.util.RsqlUtil
import java.lang.reflect.Field

class FieldMetadata {
    val parent: FieldMetadata?
    val field: Field?
    val fieldSelector: String?
    val clazz: Class<*>?
    val parameterizedType: Class<*>?
    val entityType: EntityType?

    constructor(type: Class<*>, parent: FieldMetadata?) {
        this.fieldSelector = null
        this.parent = parent
        this.field = null
        this.clazz = type
        this.parameterizedType = null
        this.entityType = null
    }

    constructor(fieldSelector: String, parent: FieldMetadata) {
        this.fieldSelector = fieldSelector
        this.parent = parent
        this.field = getField(parent, fieldSelector)
        this.clazz = this.field?.type
        this.parameterizedType = RsqlUtil.getParameterizedType(this.field)
        this.entityType = RsqlUtil.getEntityType(parameterizedType ?: clazz)
    }

    constructor(fieldSelector: String, rootClass: Class<*>) {
        this.fieldSelector = fieldSelector
        this.parent = null
        this.field = getField(rootClass, fieldSelector, this)
        this.clazz = this.field?.type
        this.parameterizedType = RsqlUtil.getParameterizedType(this.field)
        this.entityType = RsqlUtil.getEntityType(parameterizedType ?: clazz)
    }

    companion object {
        private fun getField(fieldMetadata: FieldMetadata, fieldName: String): Field? {
            val clazz = fieldMetadata.parameterizedType ?: fieldMetadata.clazz

            return this.getField(clazz, fieldName, fieldMetadata)
        }

        private fun getField(clazz: Class<*>?, fieldName: String, fieldMetadata: FieldMetadata): Field? {
            return clazz?.let {
                try {
                    it.getDeclaredField(fieldName)
                } catch (_: NoSuchFieldException) {
                    return if (it.superclass == null) {
                        null
                    } else {
                        getField(it.superclass, fieldName, fieldMetadata)
                    }
                }
            }
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
                val fieldMetadata = if (i == 0) {
                    FieldMetadata(nestedFields[i], rootClass)
                } else {
                    FieldMetadata(nestedFields[i], fieldMetadataList[i - 1])
                }

                fieldMetadataList.add(fieldMetadata)
            }

            return fieldMetadataList
        }
    }
}
