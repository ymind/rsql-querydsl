package team.yi.rsql.querydsl.test.kotlintest.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import team.yi.rsql.querydsl.util.DateUtil

@Suppress("SpellCheckingInspection")
class DateUtilTests {
    private val log = LoggerFactory.getLogger(DateUtilTests::class.java)

    private val data = mapOf(
        "dd MMMM yyyy HH:mm:ss" to "19 july 2022 13:7:24",
        "dd MMM yyyy HH:mm:ss" to "19 jul 2022 13:47:24",
        "yyyy/MM/dd HH:mm:ss" to "2022/07/19 13:47:24",
        "MM/dd/yyyy HH:mm:ss" to "07/19/2022 13:47:24",
        "yyyy-MM-dd'?T'?HH:mm:ss" to "2022-07-19T13:47:24",
        "yyyy-MM-dd'?T'?HH:mm:ss'?Z'?" to "2022-07-19T13:47:24Z",
        "yyyy-MM-dd'?T'?HH:mm:ss.SSS'?Z'?" to "2022-07-19T13:47:24.345Z",
        "yyyy-MM-dd'?T'?HH:mm:ss.SSS" to "2022-07-19T13:47:24.345",
        "yyyy-MM-dd HH:mm:ss" to "2022-07-19 13:47:24",
        "dd-MM-yyyy HH:mm:ss" to "19-07-2022 13:47:24",
        "yyyyMMdd HHmmss" to "20220719 134724",
        "dd MMMM yyyy HH:mm" to "19 july 2022 13:47",
        "dd MMM yyyy HH:mm" to "19 jul 2022 13:47",
        "yyyy/MM/dd HH:mm" to "2022/07/19 13:47",
        "MM/dd/yyyy HH:mm" to "07/19/2022 13:47",
        "yyyy-MM-dd HH:mm" to "2022-07-19 13:47",
        "yyyy.MM.dd HH:mm" to "2022.07.19 13:47",
        "dd-MM-yyyy HH:mm" to "19-07-2022 13:47",
        "yyyyMMdd HHmm" to "20220719 1347",
        "dd MMMM yyyy" to "19 july 2022",
        "dd MMM yyyy" to "19 jul 2022",
        "yyyy/MM/dd" to "2022/07/19",
        "MM/dd/yyyy" to "07/19/2022",
        "yyyy.MM.dd" to "2022.07.19",
        "yyyy-MM-dd" to "2022-07-19",
        "dd.MM.yyyy" to "19.07.2022",
        "dd-MM-yyyy" to "19-07-2022",
        "yyyy" to "2022",
        "yy-MM-dd HH:mm" to "22-07-19 13:47",
        "yy.MM.dd" to "22.07.19",
        "yy-MM-dd" to "22-07-19",
        "yy-MM" to "22-07",
        "yyyy.MM" to "2022.07",
        "yyyy-MM" to "2022-07",
        "MM.yyyy" to "07.2022",
        "MM-yyyy" to "07-2022",
    )

    @Test
    fun testDetermineDateFormat() {
        data.forEach { (format, value) ->
            val dateFormat = DateUtil.determineDateFormat(value)

            log.info("{} - {}", format, dateFormat)

            assertEquals(format.replace("'?", "'"), dateFormat)
        }
    }

    @Test
    fun testParse() {
        data.forEach { (format, value) ->
            val date = DateUtil.parse(value)

            log.info("{} - {}", format, date)

            assertNotNull(date)
        }
    }
}
