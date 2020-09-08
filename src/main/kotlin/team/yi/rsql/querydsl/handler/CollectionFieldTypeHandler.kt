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

@Suppress("UNCHECKED_CAST", "ReplaceCallWithBinaryOperator")
open class CollectionFieldTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    override fun supports(type: Class<*>): Boolean {
        val typeSystem = DefaultTypeSystem()

        return typeSystem.isCollectionType(type)
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        val listMetadata = PathMetadataFactory.forProperty(parentPath as Path<*>?, fieldMetadata.fieldSelector)
        val queryType = fieldMetadata.pathType as Class<SimpleExpression<E>>
        val collectionPath = Expressions.collectionPath(fieldMetadata.collectionType as Class<E>, queryType, listMetadata)

        return collectionPath.any() as Path<*>
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isNullOrEmpty()) return null

        val fieldMetadata = fm ?: this.fieldMetadata

        if (fieldMetadata.parameterizedType == null) return null

        val fmd = FieldMetadata(fieldMetadata.collectionType, fieldMetadata)

        try {
            val fieldType = rsqlConfig.getFieldTypeHandler(fieldMetadata.collectionType, node, operator, fmd)

            return fieldType.getValue(values, rootPath, fmd)
        } catch (e: TypeNotSupportedException) {
            e.printStackTrace()
        }

        return null
    }
}
