package team.yi.rsql.querydsl.handler

import com.querydsl.core.alias.DefaultTypeSystem
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.SimpleExpression
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.FieldMetadata
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.exception.TypeNotSupportedException
import team.yi.rsql.querydsl.operator.RsqlOperator

@Suppress("UNCHECKED_CAST")
open class CollectionFieldTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>?): Boolean {
        return if (type == null) false else DefaultTypeSystem().isCollectionType(type)
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return fieldMetadata.pathType?.let {
            val queryType = it as Class<SimpleExpression<E>>
            val listMetadata = PathMetadataFactory.forProperty(parentPath as Path<*>?, fieldMetadata.fieldSelector)
            val collectionPath = Expressions.collectionPath(fieldMetadata.collectionType as Class<E>, queryType, listMetadata)

            return collectionPath.any() as Path<*>
        }
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        val fieldMetadata = fm ?: this.fieldMetadata

        if (fieldMetadata.parameterizedType == null) return null

        fieldMetadata.collectionType?.let {
            val fmd = FieldMetadata(it, fieldMetadata)

            try {
                val typeHandler = rsqlConfig.getFieldTypeHandler(it, node, operator, fmd)

                return typeHandler.getValue(values, rootPath, fmd)
            } catch (e: TypeNotSupportedException) {
                e.printStackTrace()
            }
        }

        return null
    }
}
