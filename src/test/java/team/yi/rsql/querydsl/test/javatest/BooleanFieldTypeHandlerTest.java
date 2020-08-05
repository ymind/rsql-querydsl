package team.yi.rsql.querydsl.test.javatest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.yi.rsql.querydsl.QuerydslRsql;
import team.yi.rsql.querydsl.model.Car;
import team.yi.rsql.querydsl.test.BaseRsqlTest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanFieldTypeHandlerTest extends BaseRsqlTest {
    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldHandleBooleanIsNull() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=isnull=1")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `isnull` operator for Boolean type correctly");
        assertTrue(cars.isEmpty(), "Can't handle `isnull` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanNotNull() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=notnull=1")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `notnull` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `notnull` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanEquals() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active!=true")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `ne` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `ne` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanIn() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=in=(true,false)")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `in` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `in` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanNotIn() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=notin=(true,false)")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `notin` operator for Boolean type correctly");
        assertTrue(cars.isEmpty(), "Can't handle `notin` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanBetween() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=between=(false,true)")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `between` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `between` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanNotBetween() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=notbetween=(false,true)")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `between` operator for Boolean type correctly");
        assertTrue(cars.isEmpty(), "Can't handle `between` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanGreater() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=gt=false")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `gt` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `gt` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanGreaterOrEquals() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=goe=false")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `goe` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `goe` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanLessThan() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=lt=true")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `lt` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `lt` operator for Boolean type");
    }

    @Test
    public void shouldHandleBooleanLessThanOrEquals() {
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<Car>(this.entityManager)
            .selectFrom("Car")
            .where("active=loe=true")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "Can't handle `loe` operator for Boolean type correctly");
        assertFalse(cars.isEmpty(), "Can't handle `loe` operator for Boolean type");
    }
}
