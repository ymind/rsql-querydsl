package team.yi.rsql.querydsl.test.kotlintest.handler

import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import cz.jirutka.rsql.parser.ast.ComparisonNode
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.handler.FieldTypeHandler
import team.yi.rsql.querydsl.operator.RsqlOperator

class CarNameFieldTypeHandler(
    override val node: ComparisonNode,
    override val operator: RsqlOperator,
    override val fieldMetadata: FieldMetadata,
    override val rsqlConfig: RsqlConfig,
) : FieldTypeHandler {
    override fun supports(type: Class<*>?): Boolean {
        return node.selector == "customField"
    }

    override fun getPath(parentPath: Expression<*>?): Expression<*>? {
        return null
    }

    override fun getValue(values: List<String?>, rootPath: Path<*>, fm: FieldMetadata?): Collection<Expression<out Any?>?>? {
        return null
    }

    override fun getExpression(path: Expression<*>?, values: Collection<Expression<out Any?>?>?, fm: FieldMetadata?): BooleanExpression? {
        val left = Expressions.stringPath("name")

        return left.containsIgnoreCase("3")
    }
}
