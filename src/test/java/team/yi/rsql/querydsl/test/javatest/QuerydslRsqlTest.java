package team.yi.rsql.querydsl.test.javatest;

import com.querydsl.core.Tuple;
import cz.jirutka.rsql.parser.UnknownOperatorException;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.NodesFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.yi.rsql.querydsl.*;
import team.yi.rsql.querydsl.model.Car;
import team.yi.rsql.querydsl.operator.RsqlOperator;
import team.yi.rsql.querydsl.test.BaseRsqlTest;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
public class QuerydslRsqlTest extends BaseRsqlTest {
    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldReadRsqlConfig() {
        RsqlConfig<Car> config = new RsqlConfig.Builder<Car>(this.entityManager).build();
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<>(config)
            .from(Car.class)
            .where("description=notempty=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't read config");
        assertEquals(50, cars.size(), "Can't read config correctly");
    }

    @Test
    public void shouldRewriteQueryWithRsqlNodeInterceptor() {
        RsqlConfig<Car> config = new RsqlConfig.Builder<Car>(this.entityManager)
            .nodeInterceptor(() -> new RsqlNodeInterceptor() {
                @Override
                public <E> boolean supports(@NotNull Class<E> rootClass, @NotNull ComparisonNode comparisonNode, @NotNull RsqlOperator operator) {
                    return comparisonNode.getSelector().equalsIgnoreCase("description");
                }

                @Override
                public <E> ComparisonNode visit(
                    @NotNull Class<E> rootClass,
                    @NotNull ComparisonNode comparisonNode,
                    @NotNull RsqlOperator operator,
                    @NotNull NodesFactory nodesFactory
                ) throws UnknownOperatorException {
                    List<String> arguments = Collections.singletonList("");

                    return nodesFactory.createComparisonNode(
                        "=isnull=",
                        comparisonNode.getSelector(),
                        arguments
                    );
                }
            })
            .build();
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<>(config)
            .from(Car.class)
            .where("description=notempty=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertTrue(cars.isEmpty(), "Rewrite failed");
    }

    @Test
    public void shouldHandleMultiLevelQuery() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .from("Car")
            .where("engine.screws.name=con='name'")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't handle multi level field query");
        assertEquals(50, cars.size(), "Can't handle multi level field query correctly");
    }

    @Test
    public void shouldReturnEmptyListHandleMultiLevelQuery() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .from("Car")
            .where("engine.screws.name=con='Eszti'")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertTrue(cars.isEmpty(), "Can't handle multi level field query");
    }

    @Test
    public void shouldHandleSelectFromString() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .from("Car")
            .where("id=notnull=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't handle select from with String");
        assertEquals(50, cars.size(), "Can't handle select from with String correctly");
    }

    @Test
    public void shouldHandleSelectFromClass() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .from("Car")
            .where("id=notnull=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't handle select from with Class");
        assertEquals(50, cars.size(), "Can't handle select from with Class correctly");
    }

    @Test
    public void shouldHandleNotNullDate() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .from("Car")
            .where("mfgdt=notnull=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't handle `notnull` operator for Date type");
        assertEquals(50, cars.size(), "Can't handle `notnull` operator for Date type correctly");
    }

    @Test
    public void shouldHandleNumberIn() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .from("Car")
            .where("id=in=(3,6,9)")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't handle `in` operator for Number type");
        assertEquals(3, cars.size(), "Can't handle `in` operator for Number type correctly");
    }

    @Test
    public void shouldReturnTupleWithSelectExpression() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .build();
        List<Object> items = rsql.fetch();
        List<Tuple> cars = (items == null ? new ArrayList<Tuple>() : items).stream()
            .map(x -> (Tuple) x)
            .collect(Collectors.toList());

        assertFalse(cars.isEmpty(), "Can't handle select expression");
        assertEquals(50, cars.size(), "Can't handle select expression correctly");
        assertEquals(2, cars.get(0).toArray().length, "More than two column");
    }

    @Test
    public void shouldReturnTupleInDescOrder() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .build();
        List<Object> items = rsql.fetch();
        List<Tuple> cars = (items == null ? new ArrayList<Tuple>() : items).stream()
            .map(x -> (Tuple) x)
            .collect(Collectors.toList());

        assertFalse(cars.isEmpty(), "Can't handle select expression");
        assertEquals(50, cars.size(), "Can't handle select expression correctly");
        assertEquals(2, cars.get(0).toArray().length, "More than two column");
        assertTrue(cars.get(0).toArray()[0].equals("Béla49") && cars.get(1).toArray()[0].equals("Béla48"), "Not in order");
    }

    @Test
    public void shouldReturnTupleWithPageNumber() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .page(1, 15)
            .build();
        List<Object> items = rsql.fetch();
        List<Tuple> cars = (items == null ? new ArrayList<Tuple>() : items).stream()
            .map(x -> (Tuple) x)
            .collect(Collectors.toList());

        assertFalse(cars.isEmpty(), "Can't handle page expression");
        assertEquals(15, cars.size(), "Can't handle page expression");
        assertEquals(2, cars.get(0).toArray().length, "More than two column");
        assertTrue(cars.get(0).toArray()[0].equals("Béla34") && cars.get(1).toArray()[0].equals("Béla33"), "Not in order");
    }

    @Test
    public void shouldReturnTupleWithLimitNumber() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .limit(15L, 15L)
            .build();
        List<Object> items = rsql.fetch();
        List<Tuple> cars = (items == null ? new ArrayList<Tuple>() : items).stream()
            .map(x -> (Tuple) x)
            .collect(Collectors.toList());

        assertFalse(cars.isEmpty(), "Can't handle limit expression");
        assertEquals(15, cars.size(), "Can't handle limit expression");
        assertEquals(2, cars.get(0).toArray().length, "More than two column");
        assertTrue(cars.get(0).toArray()[0].equals("Béla34") && cars.get(1).toArray()[0].equals("Béla33"), "Not in order");
    }
}
