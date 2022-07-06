package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.*
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.test.BaseRsqlTest

class StringFieldTypeHandlerTest : BaseRsqlTest() {
    @Test
    fun shouldHandleLike() {
        val rsql = QuerydslRsql.Builder<Car>(rsqlConfig)
            .from("Car")
            .where("name=like=%la3%")
            .build()
        val cars = rsql.buildJPAQuery().fetch()

        Assertions.assertNotNull(cars, "Can't handle `like` operator for String type correctly")

        cars?.let {
            Assertions.assertFalse(cars.isEmpty(), "Can't handle `like` operator for String type")
        }
    }
}
