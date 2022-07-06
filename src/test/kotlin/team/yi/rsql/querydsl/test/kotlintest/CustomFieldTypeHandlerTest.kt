package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.exception.RsqlException
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.test.BaseRsqlTest
import team.yi.rsql.querydsl.test.kotlintest.handler.CustomFieldTypeHandler

@Suppress("SpellCheckingInspection")
class CustomFieldTypeHandlerTest : BaseRsqlTest() {
    @Test
    fun shouldReadRsqlConfigWithOperator() {
        val rsqlConfig = RsqlConfig.Builder(entityManager)
            .operator(RsqlOperator("=customnotempty="))
            .fieldTypeHandler(CustomFieldTypeHandler::class.java)
            .build()
        val rsql = QuerydslRsql.Builder<Car>(rsqlConfig)
            .from("Car")
            .where("description=customnotempty=''")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't read config with custom operator")
            assertEquals(50, cars.size, "Can't read config correctly with custom operator")
        }
    }

    @Test
    fun shouldNotFindCustomOperator() {
        assertThrows(RsqlException::class.java) {
            val rsqlConfig = RsqlConfig.Builder(entityManager)
                .fieldTypeHandler(CustomFieldTypeHandler::class.java)
                .build()
            val rsql = QuerydslRsql.Builder<Car>(rsqlConfig)
                .from("Car")
                .where("description=customnotempty=''")
                .build()

            rsql.buildJPAQuery().fetch()
        }
    }

    @Test
    fun shouldReadRsqlConfigWithFieldTypeHandler() {
        val rsqlConfig = RsqlConfig.Builder(entityManager)
            .fieldTypeHandler(CustomFieldTypeHandler::class.java)
            .build()
        val rsql = QuerydslRsql.Builder<Car>(rsqlConfig)
            .from("Car")
            .where("description=notempty=''")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        assertNotNull(cars, "result is null")

        cars?.let {
            assertFalse(cars.isEmpty(), "Can't read config with custom field type handler")
            assertEquals(50, cars.size, "Can't read config correctly with custom field type handler")
        }
    }
}
