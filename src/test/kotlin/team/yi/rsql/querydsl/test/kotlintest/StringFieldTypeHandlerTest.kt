package team.yi.rsql.querydsl.test.kotlintest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import team.yi.rsql.querydsl.QuerydslRsql
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.test.BaseRsqlTest
import javax.persistence.EntityManager

class StringFieldTypeHandlerTest : BaseRsqlTest() {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun shouldHandleLike() {
        val rsql = QuerydslRsql.Builder<Car>(entityManager)
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
