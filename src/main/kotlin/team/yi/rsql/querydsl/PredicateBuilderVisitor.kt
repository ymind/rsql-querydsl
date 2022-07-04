package team.yi.rsql.querydsl

import com.querydsl.core.types.Ops
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.PathBuilder
import cz.jirutka.rsql.parser.ast.*
import team.yi.rsql.querydsl.operator.RsqlOperator

class PredicateBuilderVisitor<E>(
    private val rootPath: PathBuilder<E>,
    private val predicateBuilder: PredicateBuilder<E>,
) : RSQLVisitor<Predicate, Predicate> {
    override fun visit(node: AndNode, param: Predicate?): Predicate = getLogicalExpression(node, param, Ops.AND)

    override fun visit(node: OrNode, param: Predicate?): Predicate = getLogicalExpression(node, param, Ops.OR)

    override fun visit(node: ComparisonNode, param: Predicate?): Predicate? = predicateBuilder.getExpression(rootPath, node, RsqlOperator(node.operator.symbol))

    private fun getLogicalExpression(node: LogicalNode, param: Predicate?, logicalOperator: Ops): BooleanExpression {
        val children = node.children.toMutableList()
        val firstNode = children.removeAt(0)
        var predicate = firstNode.accept(this, param) as BooleanExpression

        children
            .asSequence()
            .map { it.accept(this, param) as BooleanExpression }
            .forEach { predicate = combineLogicalExpression(logicalOperator, predicate, it) }

        return predicate
    }

    private fun combineLogicalExpression(logicalOperator: Ops, predicate: BooleanExpression, subPredicate: Predicate): BooleanExpression {
        return when {
            Ops.AND == logicalOperator -> predicate.and(subPredicate)
            Ops.OR == logicalOperator -> predicate.or(subPredicate)
            else -> predicate
        }
    }
}
