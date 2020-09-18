package team.yi.rsql.querydsl.util

import java.text.SimpleDateFormat
import java.time.Instant
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
    )

    fun parse(dateString: String): Date? {
        return parse(dateString, determineDateFormat(dateString) ?: return null)
    }

    fun parse(dateString: String, dateFormat: String): Date? {
        val format = if (dateFormat.isBlank()) return null else dateFormat

        return try {
            val sdf = SimpleDateFormat(format)
            sdf.isLenient = false

            sdf.parse(dateString.replace("'?", "'"))
        } catch (e: Exception) {
            try {
                Date.from(Instant.parse(dateString))
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun determineDateFormat(dateString: String): String? {
        for ((t, u) in DATE_FORMATS) {
            if (u.matches(dateString)) return t
        }

        return null
    }
}
