package team.yi.rsql.querydsl

import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.exception.TypeNotSupportedException
import team.yi.rsql.querydsl.handler.FieldTypeHandler
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil

class PredicateBuilder<E>(val rsqlConfig: RsqlConfig<E>) {
    @Throws(TypeNotSupportedException::class)
    fun getExpression(rootClass: Class<E>, node: ComparisonNode, operator: RsqlOperator): BooleanExpression? {
        val fieldMetadataList = RsqlUtil.parseFieldSelector(rootClass, node.selector)
        val rootPath = Expressions.path(rootClass, rootClass.simpleName.toLowerCase())
        val processedPaths = mutableListOf<Expression<*>>()
        var fieldType: FieldTypeHandler<E>? = null

        for (i in fieldMetadataList.indices) {
            fieldType = rsqlConfig.getFieldTypeHandler(node, operator, fieldMetadataList[i])

            val path = fieldType.getPath(if (i == 0) rootPath else processedPaths[i - 1])

            path?.let { processedPaths.add(it) }
        }

        if (fieldType == null) return null

        val values = fieldType.getValue(node.arguments, rootPath, fieldMetadataList[fieldMetadataList.size - 1])

        return fieldType.getExpression(processedPaths[processedPaths.size - 1], values, fieldMetadataList[fieldMetadataList.size - 1])
    }
}
