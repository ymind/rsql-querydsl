package team.yi.rsql.querydsl.test.javatest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.yi.rsql.querydsl.QuerydslRsql;
import team.yi.rsql.querydsl.RsqlConfig;
import team.yi.rsql.querydsl.exception.RsqlException;
import team.yi.rsql.querydsl.model.Car;
import team.yi.rsql.querydsl.operator.RsqlOperator;
import team.yi.rsql.querydsl.test.BaseRsqlTest;
import team.yi.rsql.querydsl.test.javatest.handler.CustomFieldTypeHandler;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
public class CustomFieldTypeHandlerTest extends BaseRsqlTest {
    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldReadRsqlConfigWithOperator() {
        RsqlConfig<Car> config = new RsqlConfig.Builder<Car>(this.entityManager)
            .operator(new RsqlOperator("=customnotempty="))
            .javaFieldTypeHandler(CustomFieldTypeHandler.class)
            .build();
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<>(config)
            .selectFrom("Car")
            .where("description=customnotempty=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't read config with custom operator");
        assertEquals(50, cars.size(), "Can't read config correctly with custom operator");
    }

    @Test
    public void shouldNotFindCustomOperator() {
        assertThrows(RsqlException.class, () -> {
            RsqlConfig<Car> config = new RsqlConfig.Builder<Car>(this.entityManager)
                .javaFieldTypeHandler(CustomFieldTypeHandler.class)
                .build();
            QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<>(config)
                .selectFrom("Car")
                .where("description=customnotempty=''")
                .build();

            rsql.fetch();
        });
    }

    @Test
    public void shouldReadRsqlConfigWithFieldTypeHandler() {
        RsqlConfig<Car> config = new RsqlConfig.Builder<Car>(this.entityManager)
            .javaFieldTypeHandler(CustomFieldTypeHandler.class)
            .build();
        QuerydslRsql<Car> rsql = new QuerydslRsql.Builder<>(config)
            .selectFrom("Car")
            .where("description=notempty=''")
            .build();
        List<Object> cars = rsql.fetch();

        assertNotNull(cars, "result is null");
        assertFalse(cars.isEmpty(), "Can't read config with custom field type handler");
        assertEquals(50, cars.size(), "Can't read config correctly with custom field type handler");
    }
}
