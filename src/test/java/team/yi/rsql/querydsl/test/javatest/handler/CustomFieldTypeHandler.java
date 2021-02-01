package team.yi.rsql.querydsl.test.javatest.handler;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.yi.rsql.querydsl.FieldMetadata;
import team.yi.rsql.querydsl.RsqlConfig;
import team.yi.rsql.querydsl.handler.FieldTypeHandler;
import team.yi.rsql.querydsl.operator.Operator;
import team.yi.rsql.querydsl.operator.RsqlOperator;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class CustomFieldTypeHandler<E> implements FieldTypeHandler<E> {
    private ComparisonNode node;
    private RsqlOperator operator;
    private FieldMetadata fieldMetadata;
    private RsqlConfig<E> rsqlConfig;

    @Override
    public ComparisonNode getNode() {
        return this.node;
    }

    public void setNode(final ComparisonNode node) {
        this.node = node;
    }

    @Override
    public RsqlOperator getOperator() {
        return this.operator;
    }

    public void setOperator(final RsqlOperator operator) {
        this.operator = operator;
    }

    @NotNull
    @Override
    public FieldMetadata getFieldMetadata() {
        return this.fieldMetadata;
    }

    public void setFieldMetadata(final FieldMetadata fieldMetadata) {
        this.fieldMetadata = fieldMetadata;
    }

    @NotNull
    @Override
    public RsqlConfig<E> getRsqlConfig() {
        return this.rsqlConfig;
    }

    public void setRsqlConfig(final RsqlConfig<E> rsqlConfig) {
        this.rsqlConfig = rsqlConfig;
    }

    @Override
    public boolean supports(@Nullable final Class<?> type) {
        return String.class.equals(type);
    }

    @Override
    public Expression<?> getPath(final Expression<?> parentPath) {
        String property = Objects.requireNonNull(fieldMetadata.getFieldSelector());

        return Expressions.stringPath((Path<?>) parentPath, property);
    }

    @Nullable
    @Override
    public Collection<Expression<?>> getValue(
        final List<String> values,
        final Path<?> rootPath,
        final FieldMetadata fm
    ) {
        if (values == null || values.isEmpty()) return null;

        return values.stream().map(Expressions::asSimple).collect(Collectors.toList());
    }

    @Nullable
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public BooleanExpression getExpression(
        @Nullable final Expression<?> path,
        @Nullable final Collection<? extends Expression<?>> values,
        @Nullable final FieldMetadata fm
    ) {
        if (this.operator == null) return null;

        StringExpression left = (StringExpression) path;
        StringExpression[] right = (values == null ? new ArrayList<StringExpression>() : values).stream()
            .map(x -> (StringExpression) x)
            .toArray(StringExpression[]::new);

        RsqlOperator op = this.operator;

        if (right.length == 1) {
            if (this.operator == RsqlOperator.getIn())
                op = RsqlOperator.getEquals();
            else if (this.operator == RsqlOperator.getNotIn()) {
                op = RsqlOperator.getNotEquals();
            }
        }

        if (op.equals(Operator.EQUALS_IGNORECASE)) return left.equalsIgnoreCase(right[0]);
        else if (op.equals(Operator.NOTEQUALS_IGNORECASE)) return left.notEqualsIgnoreCase(right[0]);
        else if (op.equals(Operator.LIKE)) return left.like(right[0]);
        else if (op.equals(Operator.LIKE_IGNORECASE)) return left.likeIgnoreCase(right[0]);
        else if (op.equals(Operator.STARTWITH)) return left.startsWith(right[0]);
        else if (op.equals(Operator.STARTWITH_IGNORECASE)) return left.startsWithIgnoreCase(right[0]);
        else if (op.equals(Operator.ENDWITH)) return left.endsWith(right[0]);
        else if (op.equals(Operator.ENDWITH_IGNORECASE)) return left.endsWithIgnoreCase(right[0]);
        else if (op.equals(Operator.ISEMPTY)) return left.isEmpty();
        else if (op.equals(Operator.ISNOTEMPTY)) return left.isNotEmpty();
        else if (op.equals(Operator.CONTAINS)) return left.contains(right[0]);
        else if (op.equals(Operator.CONTAINS_IGNORECASE)) return left.containsIgnoreCase(right[0]);
        else if (op.equals(Operator.IN)) return left.in(right);
        else if (op.equals(Operator.NOTIN)) return left.notIn(right);
        else return left.isNotEmpty();
    }
}
