package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.*
import team.yi.rsql.querydsl.*
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.test.BaseRsqlTest
import team.yi.rsql.querydsl.test.kotlintest.handler.CarNameFieldTypeHandler

class CarNameFieldTypeHandlerTest : BaseRsqlTest() {
    @Test
    fun shouldReadRsqlConfigWithFieldTypeHandler() {
        val rsqlConfig = RsqlConfig.Builder(entityManager)
            .fieldTypeHandler(CarNameFieldTypeHandler::class.java)
            .build()
        val rsql = QuerydslRsql.Builder<Car>(rsqlConfig)
            .from("Car")
            // NOTE: this operator and value will not work
            .where("customField=eq='xxx'")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        Assertions.assertNotNull(cars, "result is null")

        cars?.let {
            Assertions.assertFalse(cars.isEmpty(), "Can't read config with custom field type handler")
            Assertions.assertEquals(14, cars.size, "Can't read config correctly with custom field type handler")
        }
    }
}
