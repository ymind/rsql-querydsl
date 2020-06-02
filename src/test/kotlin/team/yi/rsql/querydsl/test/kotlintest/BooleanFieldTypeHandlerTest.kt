package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.test.BaseRsqlTest
import javax.persistence.EntityManager

class BooleanFieldTypeHandlerTest : BaseRsqlTest() {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun shouldHandleBooleanIsNull() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=isnull=1")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `isnull` operator for Boolean type correctly")

        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `isnull` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanNotNull() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=notnull=1")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `notnull` operator for Boolean type correctly")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `notnull` operator for Boolean type")
        }
    }

    @Test
    fun shouldHandleBooleanEquals() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active==true")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `eq` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `eq` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanNotEquals() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active!=true")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `ne` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `ne` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanIn() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=in=(true,false)")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `in` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `in` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanNotIn() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=notin=(true,false)")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `notin` operator for Boolean type correctly")

        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `notin` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanBetween() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=between=(false,true)")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `between` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `between` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanNotBetween() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=notbetween=(false,true)")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `between` operator for Boolean type correctly")

        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `between` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanGreater() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=gt=false")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `gt` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `gt` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanGreaterOrEquals() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=goe=false")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `goe` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `goe` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanLessThan() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=lt=true")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `lt` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `lt` operator for Boolean type") }
    }

    @Test
    fun shouldHandleBooleanLessThanOrEquals() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .selectFrom("Car")
            .where("active=loe=true")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "Can't handle `loe` operator for Boolean type correctly")
        cars?.let { assertFalse(cars.isEmpty(), "Can't handle `loe` operator for Boolean type") }
    }
}
