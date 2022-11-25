package team.yi.rsql.querydsl

import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.handler.FieldTypeHandler
import team.yi.rsql.querydsl.operator.RsqlOperator

class PredicateBuilder<E>(private val rsqlConfig: RsqlConfig) {
    fun getExpression(rootPath: PathBuilder<E>, comparisonNode: ComparisonNode, operator: RsqlOperator): BooleanExpression? {
        val rootClass = rootPath.type
        val interceptor = rsqlConfig.nodeInterceptors.find { it.supports(rootClass, comparisonNode, operator) }
        val node = if (interceptor == null) comparisonNode else interceptor.visit(rootClass, comparisonNode, operator, rsqlConfig.nodesFactory) ?: return null
        val fieldMetadataList = FieldMetadata.parseFieldSelector(rootClass, node.selector)
        val processedPaths = mutableListOf<Expression<*>>()
        var typeHandler: FieldTypeHandler? = null

        for (i in fieldMetadataList.indices) {
            typeHandler = rsqlConfig.getFieldTypeHandler(node, fieldMetadataList[i])

            val path = typeHandler.getPath(if (i == 0) rootPath else processedPaths[i - 1])

            path?.let { processedPaths.add(it) }
        }

        if (typeHandler == null) return null

        val values = typeHandler.getValue(node.arguments, rootPath, fieldMetadataList[fieldMetadataList.size - 1])

        return when {
            processedPaths.isEmpty() -> typeHandler.getExpression(null, values, fieldMetadataList[fieldMetadataList.size - 1])
            else -> typeHandler.getExpression(processedPaths[processedPaths.size - 1], values, fieldMetadataList[fieldMetadataList.size - 1])
        }
    }
}
