package team.yi.rsql.querydsl.test.javatest.handler;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.yi.rsql.querydsl.FieldMetadata;
import team.yi.rsql.querydsl.RsqlConfig;
import team.yi.rsql.querydsl.handler.FieldTypeHandler;
import team.yi.rsql.querydsl.operator.Operator;
import team.yi.rsql.querydsl.operator.RsqlOperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomFieldTypeHandler<E> implements FieldTypeHandler<E> {
    private ComparisonNode node;
    private RsqlOperator operator;
    private FieldMetadata fieldMetadata;
    private RsqlConfig<E> rsqlConfig;

    @Nullable
    @Override
    public ComparisonNode getNode() {
        return this.node;
    }

    @Nullable
    @Override
    public RsqlOperator getOperator() {
        return this.operator;
    }

    @NotNull
    @Override
    public FieldMetadata getFieldMetadata() {
        return this.fieldMetadata;
    }

    @NotNull
    @Override
    public RsqlConfig<E> getConfig() {
        return this.rsqlConfig;
    }

    @Override
    public boolean supportsType(@NotNull final Class<?> type) {
        return String.class.equals(type);
    }

    @Nullable
    @Override
    public Expression<?> getPath(@Nullable final Expression<?> parentPath) {
        String property = Objects.requireNonNull(fieldMetadata.getFieldSelector());

        return Expressions.stringPath((Path<?>) parentPath, property);
    }

    @Nullable
    @Override
    public Collection<Expression<?>> getValue(
        @Nullable final List<String> values,
        @Nullable final Path<?> rootPath,
        @Nullable final FieldMetadata fm
    ) {
        if (values == null || values.isEmpty()) return null;

        return values.stream().map(Expressions::asSimple).collect(Collectors.toList());
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Nullable
    @Override
    public BooleanExpression getExpression(
        @NotNull final Expression<?> path,
        @Nullable final Collection<? extends Expression<?>> values,
        @Nullable final FieldMetadata fm
    ) {
        if (this.operator == null) return null;

        RsqlOperator operator = this.operator;
        StringExpression left = (StringExpression) path;
        StringExpression[] right = (values == null ? new ArrayList<StringExpression>() : values).stream()
            .map(x -> (StringExpression) x)
            .toArray(StringExpression[]::new);

        if (operator.equals(Operator.EQUALS_IGNORECASE)) return left.equalsIgnoreCase(right[0]);
        else if (operator.equals(Operator.NOTEQUALS_IGNORECASE)) return left.notEqualsIgnoreCase(right[0]);
        else if (operator.equals(Operator.LIKE)) return left.like(right[0]);
        else if (operator.equals(Operator.LIKE_IGNORECASE)) return left.likeIgnoreCase(right[0]);
        else if (operator.equals(Operator.STARTWITH)) return left.startsWith(right[0]);
        else if (operator.equals(Operator.STARTWITH_IGNORECASE)) return left.startsWithIgnoreCase(right[0]);
        else if (operator.equals(Operator.ENDWITH)) return left.endsWith(right[0]);
        else if (operator.equals(Operator.ENDWITH_IGNORECASE)) return left.endsWithIgnoreCase(right[0]);
        else if (operator.equals(Operator.ISEMPTY)) return left.isEmpty();
        else if (operator.equals(Operator.ISNOTEMPTY)) return left.isNotEmpty();
        else if (operator.equals(Operator.CONTAINS)) return left.contains(right[0]);
        else if (operator.equals(Operator.CONTAINS_IGNORECASE)) return left.containsIgnoreCase(right[0]);
        else if (operator.equals(Operator.IN)) return left.in(right);
        else if (operator.equals(Operator.NOTIN)) return left.notIn(right);
        else return left.isNotEmpty();
    }
}
