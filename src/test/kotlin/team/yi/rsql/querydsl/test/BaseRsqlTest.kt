package team.yi.rsql.querydsl.test

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.yi.rsql.querydsl.RsqlConfig
import team.yi.rsql.querydsl.model.*
import team.yi.rsql.querydsl.repository.*
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

@Suppress("UnnecessaryAbstractClass")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseRsqlTest {
    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var screwRepository: ScrewRepository

    @Autowired
    private lateinit var engineRepository: EngineRepository

    @Autowired
    protected lateinit var entityManager: EntityManager

    protected val rsqlConfig: RsqlConfig
        get() = RsqlConfig.Builder(entityManager).build()

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
            val screws = mutableListOf(savedScrew)

            val savedScrew2 = screwRepository.save(screw)
            val screws2 = mutableListOf(savedScrew2)

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
            car.createdAt = LocalDateTime.now()
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
