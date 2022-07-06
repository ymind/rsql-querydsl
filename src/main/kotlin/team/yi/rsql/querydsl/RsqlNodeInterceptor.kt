package team.yi.rsql.querydsl

import cz.jirutka.rsql.parser.UnknownOperatorException
import cz.jirutka.rsql.parser.ast.*
import team.yi.rsql.querydsl.operator.RsqlOperator

@FunctionalInterface
interface RsqlNodeInterceptor {
    fun <E> supports(rootClass: Class<E>, comparisonNode: ComparisonNode, operator: RsqlOperator): Boolean

    @Throws(UnknownOperatorException::class)
    fun <E> visit(rootClass: Class<E>, comparisonNode: ComparisonNode, operator: RsqlOperator, nodesFactory: NodesFactory): ComparisonNode?
}
