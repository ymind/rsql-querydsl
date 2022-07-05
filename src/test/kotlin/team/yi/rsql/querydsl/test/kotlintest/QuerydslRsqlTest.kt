package team.yi.rsql.querydsl.test.kotlintest

import com.querydsl.core.Tuple
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Projections
import com.querydsl.core.types.QBean
import com.querydsl.core.types.dsl.Expressions
import cz.jirutka.rsql.parser.ast.ComparisonNode
import cz.jirutka.rsql.parser.ast.NodesFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.RsqlNodeInterceptor
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.test.BaseRsqlTest
import team.yi.rsql.querydsl.util.PathFactory
import team.yi.rsql.querydsl.util.RsqlUtil
import javax.persistence.EntityManager

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class QuerydslRsqlTest : BaseRsqlTest() {
    @Autowired
    private lateinit var entityManager: EntityManager

    private val pathFactory = PathFactory()

    @Test
    fun shouldReadRsqlConfig() {
        val config = RsqlConfig.Builder(entityManager).build()
        val rsql = QuerydslRsql.Builder<Car>(config)
            .from(Car::class.java)
            .where("description=notempty=''")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't read config")
            assertEquals(50, cars.size, "Can't read config correctly")
        }
    }

    @Test
    fun shouldRewriteQueryWithRsqlNodeInterceptor() {
        val config = RsqlConfig.Builder(entityManager)
            .nodeInterceptor {
                object : RsqlNodeInterceptor {
                    override fun <E> supports(rootClass: Class<E>, comparisonNode: ComparisonNode, operator: RsqlOperator): Boolean {
                        return comparisonNode.selector.equals("description", ignoreCase = true)
                    }

                    override fun <E> visit(rootClass: Class<E>, comparisonNode: ComparisonNode, operator: RsqlOperator, nodesFactory: NodesFactory): ComparisonNode? {
                        val arguments = listOf("")

                        return nodesFactory.createComparisonNode("=isnull=", comparisonNode.selector, arguments)
                    }
                }
            }
            .build()
        val rsql = QuerydslRsql.Builder<Car>(config)
            .from(Car::class.java)
            .where("description=notempty=''")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertTrue(cars.isEmpty(), "Rewrite failed")
        }
    }

    @Test
    fun shouldHandleMultiLevelQuery() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("engine.screws.name=con='name'")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

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
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let { assertTrue(cars.isEmpty(), "Can't handle multi level field query") }
    }

    @Test
    fun shouldHandleSelectFromString() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("id=notnull=''")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

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
        val cars = rsql.buildJPAQuery().fetch()

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
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `notnull` operator for Date type")
            assertEquals(50, cars.size, "Can't handle `notnull` operator for Date type correctly")
        }
    }

    @Test
    fun shouldHandleDateRange() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("mfgdt>='2000-01-01 00:01:02' and mfgdt<='6666-12-31 23:59:59'")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

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
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `in` operator for Number type")
            assertEquals(3, cars.size, "Can't handle `in` operator for Number type correctly")
        }
    }

    @Test
    fun shouldHandleNumberInSingleValue() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("id=in=(3)")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `in` operator for Number type")
            assertEquals(1, cars.size, "Can't handle `in` operator for Number type correctly")
        }
    }

    @Test
    fun shouldHandleNumberNotInSingleValue() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
            .from("Car")
            .where("id=out=(3)")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't handle `in` operator for Number type")
            assertEquals(49, cars.size, "Can't handle `in` operator for Number type correctly")
        }
    }

    @Test
    fun shouldReturnTupleWithSelectExpression() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description")
            .from("Car")
            .where("id=notnull=''")
            .build()
        val cars: List<Tuple> = querydslRsql.buildJPAQuery().fetch() as List<Tuple>

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
        val cars: List<Tuple> = querydslRsql.buildJPAQuery().fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle select expression")
        assertEquals(50, cars.size, "Can't handle select expression correctly")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla49" && cars[1].toArray()[0] == "Béla48", "Not in order")
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
        val cars: List<Tuple> = querydslRsql.buildJPAQuery().fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle page expression")
        assertEquals(15, cars.size, "Can't handle page expression")
        assertEquals(2, cars[0].toArray().size, "More than two column")
        assertTrue(cars[0].toArray()[0] == "Béla34" && cars[1].toArray()[0] == "Béla33", "Not in order")
    }

    @Test
    fun shouldReturnTupleWithLimitNumber() {
        val clazz = Car::class.java
        val pathBuilder = pathFactory.create(clazz)
        val selectFields = RsqlUtil.parseSelect("name,description", pathBuilder).toTypedArray()
        val select: QBean<Car> = Projections.bean(
            clazz,
            pathBuilder.getNumber("id", Long::class.java).add(1000).`as`("id"),
            *selectFields,
        )
        val querydslRsql = QuerydslRsql.Builder<Car>(entityManager)
            .select(select)
            // .from(clazz)
            .where("id=notnull='' and (name=like='%a%' or name=con='Béla2,Béla11')")
            .sort("id.desc")
            .limit(15L, 15)
            .build()
        val cars = querydslRsql.buildJPAQuery(pathBuilder).fetch()

        assertFalse(cars.isEmpty(), "Can't handle limit expression")
        assertEquals(15, cars.size, "Can't handle limit expression")
    }

    @Test
    fun shouldReturnAllActived() {
        val querydslRsql: QuerydslRsql<*> = QuerydslRsql.Builder<Car>(entityManager)
            .select("name,description,active")
            .from("Car")
            .where("id=notnull=''")
            .globalPredicate(Expressions.booleanOperation(Ops.EQ, Expressions.booleanPath("active"), Expressions.asBoolean(true)))
            .sort("id.desc")
            .build()
        val cars: List<Tuple> = querydslRsql.buildJPAQuery().fetch() as List<Tuple>

        assertFalse(cars.isEmpty(), "Can't handle limit expression")
        assertTrue(cars.stream().allMatch { it.toArray()[2] as Boolean }, "Can't handle globalPredicate")
    }
}
