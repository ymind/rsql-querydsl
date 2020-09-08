package team.yi.rsql.querydsl.test

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.yi.rsql.querydsl.model.Car
import team.yi.rsql.querydsl.model.Engine
import team.yi.rsql.querydsl.model.Screw
import team.yi.rsql.querydsl.model.ScrewType
import team.yi.rsql.querydsl.repository.CarRepository
import team.yi.rsql.querydsl.repository.EngineRepository
import team.yi.rsql.querydsl.repository.ScrewRepository
import java.security.SecureRandom
import java.util.*

@Suppress("SpellCheckingInspection")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseRsqlTest {
    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var screwRepository: ScrewRepository

    @Autowired
    private lateinit var engineRepository: EngineRepository

    @BeforeAll
    fun initData() {
        if (dataLoaded) return

        val random = SecureRandom()

        (0..49).forEach {
            val randomNum = random.nextInt(100 - 1 + 1) + 1

            val screw = Screw()
            screw.name = "Screw name $randomNum"
            screw.screwType = randomEnum(ScrewType::class.java)
            screw.description = "Descreption screw $randomNum"
            screw.size = randomNum.toLong()

            val savedScrew = screwRepository.save(screw)
            val screws: MutableList<Screw> = ArrayList()
            screws.add(savedScrew)

            val savedScrew2 = screwRepository.save(screw)
            val screws2: MutableList<Screw> = ArrayList()
            screws2.add(savedScrew2)

            val engine = Engine()
            engine.name = "Engine $randomNum"
            engine.description = "Engine description $randomNum"
            engine.screws = screws2

            val savedEngine = engineRepository.save(engine)
            val car = Car()
            car.name = "Béla$it"
            car.description = "Descreption car $randomNum"
            car.active = Math.random() < 0.5
            car.mfgdt = Date()
            car.screws = screws
            car.engine = savedEngine

            carRepository.save(car)
        }

        dataLoaded = true
    }

    fun <T : Enum<*>?> randomEnum(clazz: Class<T>): T {
        val random = SecureRandom()

        return clazz.enumConstants[random.nextInt(clazz.enumConstants.size)]
    }

    companion object {
        private var dataLoaded: Boolean = false
    }
}
