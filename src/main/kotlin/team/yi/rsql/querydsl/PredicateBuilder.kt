package team.yi.rsql.querydsl

import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import java.util.*
import team.yi.rsql.querydsl.exception.TypeNotSupportedException
import team.yi.rsql.querydsl.handler.FieldTypeHandler
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil

class PredicateBuilder<E>(val rsqlConfig: RsqlConfig<E>) {
    @Throws(TypeNotSupportedException::class)
    fun getExpression(rootClass: Class<E>, node: ComparisonNode, operator: RsqlOperator): BooleanExpression? {
        val fieldMetadataList = RsqlUtil.parseFieldSelector(rootClass, node.selector)
        val rootPath = Expressions.path(rootClass, rootClass.simpleName.lowercase(Locale.getDefault()))
        val processedPaths = mutableListOf<Expression<*>>()
        var typeHandler: FieldTypeHandler<E>? = null

        for (i in fieldMetadataList.indices) {
            typeHandler = rsqlConfig.getFieldTypeHandler(node, operator, fieldMetadataList[i])

            val path = typeHandler.getPath(if (i == 0) rootPath else processedPaths[i - 1])

            path?.let { processedPaths.add(it) }
        }

        if (typeHandler == null) return null

        val values = typeHandler.getValue(node.arguments, rootPath, fieldMetadataList[fieldMetadataList.size - 1])

        return when {
            processedPaths.isNullOrEmpty() -> typeHandler.getExpression(null, values, fieldMetadataList[fieldMetadataList.size - 1])
            else -> typeHandler.getExpression(processedPaths[processedPaths.size - 1], values, fieldMetadataList[fieldMetadataList.size - 1])
        }
    }
}
