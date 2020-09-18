package team.yi.rsql.querydsl.test.kotlintest

import com.querydsl.core.Tuple
import com.querydsl.core.types.Ops
import com.querydsl.core.types.dsl.Expressions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.test.BaseRsqlTest
import javax.persistence.EntityManager

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class QuerydslRsqlTest : BaseRsqlTest() {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun shouldReadRsqlConfig() {
        val config = RsqlConfig.Builder<Car>(entityManager).build()
        val rsql = QuerydslRsql.Builder(config)
            .from(Car::class.java)
            .where("description=notempty=''")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't read config")
            assertEquals(50, cars.size, "Can't read config correctly")
        }
    }

    @Test
    fun shouldHandleMultiLevelQuery() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("engine.screws.name=con='name'")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle multi level field query")
            assertEquals(50, cars.size, "Can't handle multi level field query correctly")
        }
    }

    @Test
    fun shouldReturnEmptyListHandleMultiLevelQuery() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("engine.screws.name=con='Eszti'")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let { assertTrue(cars.isEmpty(), "Can't handle multi level field query") }
    }

    @Test
    fun shouldHandleSelectFromString() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("id=notnull=''")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle select from with String")
            assertEquals(50, cars.size, "Can't handle select from with String correctly")
        }
    }

    @Test
    fun shouldHandleSelectFromClass() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("id=notnull=''")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle select from with Class")
            assertEquals(50, cars.size, "Can't handle select from with Class correctly")
        }
    }

    @Test
    fun shouldHandleNotNullDate() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("mfgdt=notnull=''")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `notnull` operator for Date type")
            assertEquals(50, cars.size, "Can't handle `notnull` operator for Date type correctly")
        }
    }

    @Test
    fun shouldHandleNumberIn() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("id=in=(3,6,9)")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `in` operator for Number type")
            assertEquals(3, cars.size, "Can't handle `in` operator for Number type correctly")
        }
    }

    @Test
    fun shouldReturnTupleWithSelectExpression() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle select expression")
        assertEquals(50, cars.size, "Can't handle select expression correctly")
        assertEquals(2, cars[0].toArray().size, "More than two column")
    }

    @Test
    fun shouldReturnTupleInDescOrder() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle select expression")
        assertEquals(50, cars.size, "Can't handle select expression correctly")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla49" && cars[1].toArray()[0] == "Béla48", "Not in order")
    }

    @Test
    fun shouldReturnTupleWithPageString() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .page("1,15")
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle page expression")
        assertEquals(15, cars.size, "Can't handle page expression")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla34" && cars[1].toArray()[0] == "Béla33", "Not in order")
    }

    @Test
    fun shouldReturnTupleWithPageNumber() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .page(1, 15)
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle page expression")
        assertEquals(15, cars.size, "Can't handle page expression")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla34" && cars[1].toArray()[0] == "Béla33", "Not in order")
    }

    @Test
    fun shouldReturnTupleWithLimitString() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .limit("15,15")
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle limit expression")
        assertEquals(15, cars.size, "Can't handle limit expression")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla34" && cars[1].toArray()[0] == "Béla33", "Not in order")
    }

    @Test
    fun shouldReturnTupleWithLimitNumber() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .sort("id.desc")
            .limit(15L, 15)
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle limit expression")
        assertEquals(15, cars.size, "Can't handle limit expression")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla34" && cars[1].toArray()[0] == "Béla33", "Not in order")
    }

    @Test
    fun shouldReturnAllActived() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description,active")
            .from("Car")
            .where("id=notnull=''")
            .globalPredicate(
                Expressions.booleanOperation(Ops.EQ, Expressions.booleanPath("active"), Expressions.asBoolean(true))
            )
            .sort("id.desc")
            .build()
        val cars: List<Tuple> = querydslRsql.fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle limit expression")
        assertTrue(cars.stream().allMatch { it.toArray()[2] as Boolean }, "Can't handle globalPredicate")
    }
}
