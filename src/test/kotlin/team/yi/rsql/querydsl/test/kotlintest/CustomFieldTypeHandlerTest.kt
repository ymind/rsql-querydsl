package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.exception.RsqlException
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.test.BaseRsqlTest
import team.yi.rsql.querydsl.test.kotlintest.handler.CustomFieldTypeHandler
import javax.persistence.EntityManager

@Suppress("SpellCheckingInspection")
class CustomFieldTypeHandlerTest : BaseRsqlTest() {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun shouldReadRsqlConfigWithOperator() {
        val config = RsqlConfig.Builder<Car>(entityManager)
            .operator(RsqlOperator("=customnotempty="))
            .fieldTypeHandler(CustomFieldTypeHandler::class.java)
            .build()
        val rsql = QuerydslRsql.Builder(config)
            .from("Car")
            .where("description=customnotempty=''")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't read config with custom operator")
            assertEquals(50, cars.size, "Can't read config correctly with custom operator")
        }
    }

    @Test
    fun shouldNotFindCustomOperator() {
        assertThrows(RsqlException::class.java) {
            val config = RsqlConfig.Builder<Car>(entityManager)
                .fieldTypeHandler(CustomFieldTypeHandler::class.java)
                .build()
            val rsql = QuerydslRsql.Builder(config)
                .from("Car")
                .where("description=customnotempty=''")
                .build()

            rsql.fetch()
        }
    }

    @Test
    fun shouldReadRsqlConfigWithFieldTypeHandler() {
        val config = RsqlConfig.Builder<Car>(entityManager)
            .fieldTypeHandler(CustomFieldTypeHandler::class.java)
            .build()
        val rsql = QuerydslRsql.Builder(config)
            .from("Car")
            .where("description=notempty=''")
            .build()
        val cars = rsql.fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't read config with custom field type handler")
            assertEquals(50, cars.size, "Can't read config correctly with custom field type handler")
        }
    }
}
