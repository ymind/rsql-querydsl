package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.test.BaseRsqlTest
import team.yi.rsql.querydsl.test.kotlintest.handler.CarNameFieldTypeHandler
import javax.persistence.EntityManager

class CarNameFieldTypeHandlerTest : BaseRsqlTest() {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun shouldReadRsqlConfigxWithFieldTypeHandler() {
        val config = RsqlConfig.Builder<Car>(entityManager)
            .fieldTypeHandler(CarNameFieldTypeHandler::class.java)
            .build()
        val rsql = QuerydslRsql.Builder(config)
            .from("Car")
            // NOTE: this operator and value will not work
            .where("customField=eq='xxx'")
            .build()
        val cars = rsql.fetch()

        Assertions.assertNotNull(cars, "result is null")

        cars?.let {
            Assertions.assertFalse(cars.isEmpty(), "Can't read config with custom field type handler")
            Assertions.assertEquals(14, cars.size, "Can't read config correctly with custom field type handler")
        }
    }
}
