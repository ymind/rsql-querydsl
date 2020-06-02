package team.yi.rsql.querydsl

import com.querydsl.core.types.Ops
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import cz.jirutka.rsql.parser.ast.*
import team.yi.rsql.querydsl.operator.RsqlOperator

class PredicateBuilderVisitor<E>(
    private val rootClass: Class<E>,
    private val predicateBuilder: PredicateBuilder<E>
) : RSQLVisitor<Predicate, Predicate> {
    override fun visit(node: AndNode, param: Predicate): Predicate = getLogicalExpression(node, param, Ops.AND)

    override fun visit(node: OrNode, param: Predicate): Predicate = getLogicalExpression(node, param, Ops.OR)

    override fun visit(node: ComparisonNode, param: Predicate): Predicate? {
        return predicateBuilder.getExpression(rootClass, node, RsqlOperator(node.operator.symbol))
    }

    private fun getLogicalExpression(node: LogicalNode, param: Predicate, logicalOperator: Ops): BooleanExpression {
        val children: MutableList<Node> = node.children.toMutableList()
        val firstNode: Node = children.removeAt(0)
        var predicate = firstNode.accept(this, param) as BooleanExpression

        for (subNode in children) {
            val subPredicate = subNode.accept(this, param) as BooleanExpression

            predicate = combineLogicalExpression(logicalOperator, predicate, subPredicate)
        }

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
