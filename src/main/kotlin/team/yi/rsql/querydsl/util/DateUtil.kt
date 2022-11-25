package team.yi.rsql.querydsl.util

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
object DateUtil {
    private val regexOptions = setOf(RegexOption.IGNORE_CASE)

    val DATE_FORMATS: Map<String, Regex> = mapOf(
        "dd MMMM yyyy HH:mm:ss" to Regex("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "dd MMM yyyy HH:mm:ss" to Regex("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyy/MM/dd HH:mm:ss" to Regex("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "MM/dd/yyyy HH:mm:ss" to Regex("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyy-MM-dd'T'HH:mm:ss" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}'?T'?\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyy-MM-dd'T'HH:mm:ss'Z'" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}'?T'?\\d{1,2}:\\d{1,2}:\\d{1,2}'?Z'?$", regexOptions),
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}'?T'?\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3}'?Z'?$", regexOptions),
        "yyyy-MM-dd'T'HH:mm:ss.SSS" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}'?T'?\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3}$", regexOptions),
        "yyyy-MM-dd HH:mm:ss.SSS" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3}$", regexOptions),
        "yyyy-MM-dd HH:mm:ss" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "dd-MM-yyyy HH:mm:ss" to Regex("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyyMMdd HHmmss" to Regex("^\\d{8}\\s\\d{6}$", regexOptions),
        "dd MMMM yyyy HH:mm" to Regex("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "dd MMM yyyy HH:mm" to Regex("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyy/MM/dd HH:mm" to Regex("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "MM/dd/yyyy HH:mm" to Regex("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyy-MM-dd HH:mm" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyy.MM.dd HH:mm" to Regex("^\\d{4}.\\d{1,2}.\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "dd-MM-yyyy HH:mm" to Regex("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "yyyyMMdd HHmm" to Regex("^\\d{8}\\s\\d{4}$", regexOptions),
        "dd MMMM yyyy" to Regex("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", regexOptions),
        "dd MMM yyyy" to Regex("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", regexOptions),
        "yyyy/MM/dd" to Regex("^\\d{4}/\\d{1,2}/\\d{1,2}$", regexOptions),
        "MM/dd/yyyy" to Regex("^\\d{1,2}/\\d{1,2}/\\d{4}$", regexOptions),
        "yyyy.MM.dd" to Regex("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", regexOptions),
        "yyyy-MM-dd" to Regex("^\\d{4}-\\d{1,2}-\\d{1,2}$", regexOptions),
        "dd.MM.yyyy" to Regex("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", regexOptions),
        "dd-MM-yyyy" to Regex("^\\d{1,2}-\\d{1,2}-\\d{4}$", regexOptions),
        "yyyy" to Regex("^\\d{4}$", regexOptions),
        "yy-MM-dd HH:mm" to Regex("^\\d{2}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", regexOptions),
        "yy.MM.dd" to Regex("^\\d{2}\\.\\d{1,2}\\.\\d{1,2}$", regexOptions),
        "yy-MM-dd" to Regex("^\\d{2}-\\d{1,2}-\\d{1,2}$", regexOptions),
        "yy-MM" to Regex("^\\d{2}-\\d{1,2}\$", regexOptions),
        "yyyy.MM" to Regex("^\\d{4}\\.\\d{1,2}\$", regexOptions),
        "yyyy-MM" to Regex("^\\d{4}-\\d{1,2}\$", regexOptions),
        "MM.yyyy" to Regex("^\\d{1,2}\\.\\d{4}$", regexOptions),
        "MM-yyyy" to Regex("^\\d{1,2}-\\d{4}$", regexOptions),
        "HH:mm:ss.SSS'Z'" to Regex("^\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3}'?Z'?$", regexOptions),
        "HH:mm:ss'Z'" to Regex("^\\d{1,2}:\\d{1,2}:\\d{1,2}'?Z'?$", regexOptions),
        "HH:mm:ss.SSS" to Regex("^\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3}$", regexOptions),
        "HH:mm:ss" to Regex("^\\d{1,2}:\\d{1,2}:\\d{1,2}$", regexOptions),
        "HH:mm" to Regex("^\\d{1,2}:\\d{1,2}$", regexOptions),
    )

    val formatters = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,

        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_DATE,
        DateTimeFormatter.ISO_OFFSET_DATE,

        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ISO_TIME,
        DateTimeFormatter.ISO_OFFSET_TIME,

        DateTimeFormatter.ISO_ORDINAL_DATE,
        DateTimeFormatter.ISO_WEEK_DATE,
        DateTimeFormatter.BASIC_ISO_DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME,
    )

    fun parse(dateString: String): Date? {
        return parse(dateString, determineDateFormat(dateString))
    }

    @Suppress("ReturnCount")
    fun parse(dateString: String, dateFormat: String?): Date? {
        val dateStr = dateString.ifBlank { return null }.trim()

        try {
            val offsetDateTime = parseOffsetDateTime(dateStr)

            if (offsetDateTime == null) {
                if (dateFormat.isNullOrEmpty()) return null

                val format = dateFormat.ifBlank { return null }.trim()
                val sdf = SimpleDateFormat(format)
                sdf.isLenient = false

                if (dateStr.endsWith('Z')) {
                    sdf.timeZone = TimeZone.getTimeZone("+00:00:00")
                }

                return sdf.parse(dateStr)
            }

            return Date.from(offsetDateTime.toInstant())
        } catch (_: Exception) {
            val offsetDateTime = parseOffsetDateTime(dateStr) ?: return null

            return Date.from(offsetDateTime.toInstant())
        }
    }

    fun determineDateFormat(dateString: String): String? {
        return DATE_FORMATS.asSequence()
            .filter { it.value.matches(dateString) }
            .map { it.key }
            .firstOrNull()
    }

    fun parseOffsetDateTime(dateStr: String): OffsetDateTime? {
        for (formatter in formatters) {
            try {
                return OffsetDateTime.parse(dateStr, formatter)
            } catch (_: Exception) {
                continue
            }
        }

        return null
    }
}
