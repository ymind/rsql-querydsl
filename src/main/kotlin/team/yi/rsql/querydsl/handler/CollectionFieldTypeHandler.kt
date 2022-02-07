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
import team.yi.rsql.querydsl.util.RsqlUtil

@Suppress("UNCHECKED_CAST")
open class CollectionFieldTypeHandler<E>(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig<E>,
) : SimpleFieldTypeHandler<E>(node, operator, fieldMetadata, rsqlConfig) {
    private val collectionType: Class<*>?
        get() = fieldMetadata.parameterizedType ?: fieldMetadata.clazz

    override fun supports(type: Class<*>?): Boolean {
        return if (type == null) false else DefaultTypeSystem().isCollectionType(type)
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        val queryType = RsqlUtil.getPathType(fieldMetadata.entityType) as? Class<SimpleExpression<E>> ?: return null
        val listMetadata = PathMetadataFactory.forProperty(parentPath as Path<*>?, fieldMetadata.fieldSelector)
        val collectionPath = Expressions.collectionPath(collectionType as Class<E>, queryType, listMetadata)

        return collectionPath.any() as Path<*>
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        if (values.isEmpty()) return null

        val fieldMetadata = fm ?: this.fieldMetadata

        collectionType?.let {
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
