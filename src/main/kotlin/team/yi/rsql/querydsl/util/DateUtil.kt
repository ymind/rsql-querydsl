package team.yi.rsql.querydsl.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val DATE_FORMAT_REGEXPS: MutableMap<String, String> = HashMap()

    @Throws(ParseException::class)
    fun parse(dateString: String): Date? {
        return parse(dateString, determineDateFormat(dateString) ?: return null)
    }

    @Throws(ParseException::class)
    fun parse(dateString: String, dateFormat: String): Date? {
        val format = if (dateFormat.isBlank()) determineDateFormat(dateString) else dateFormat
        val simpleDateFormat = SimpleDateFormat(format ?: return null)

        simpleDateFormat.isLenient = false

        return simpleDateFormat.parse(dateString.trim('\'', '"'))
    }

    fun determineDateFormat(dateString: String): String? {
        return DATE_FORMAT_REGEXPS.keys
            .firstOrNull { dateString.toLowerCase().matches(it.toRegex()) }
            ?.let { DATE_FORMAT_REGEXPS[it] }
    }

    init {
        DATE_FORMAT_REGEXPS["^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$"] = "dd MMMM yyyy HH:mm:ss"
        DATE_FORMAT_REGEXPS["^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$"] = "dd MMM yyyy HH:mm:ss"
        DATE_FORMAT_REGEXPS["^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$"] = "yyyy/MM/dd HH:mm:ss"
        DATE_FORMAT_REGEXPS["^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$"] = "MM/dd/yyyy HH:mm:ss"
        DATE_FORMAT_REGEXPS["^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$"] = "yyyy-MM-dd HH:mm:ss"
        DATE_FORMAT_REGEXPS["^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$"] = "dd-MM-yyyy HH:mm:ss"
        DATE_FORMAT_REGEXPS["^\\d{8}\\s\\d{6}$"] = "yyyyMMdd HHmmss"
        DATE_FORMAT_REGEXPS["^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$"] = "dd MMMM yyyy HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$"] = "dd MMM yyyy HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$"] = "yyyy/MM/dd HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$"] = "MM/dd/yyyy HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$"] = "yyyy-MM-dd HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{4}.\\d{1,2}.\\d{1,2}\\s\\d{1,2}:\\d{2}$"] = "yyyy-MM-dd HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$"] = "dd-MM-yyyy HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{8}\\s\\d{4}$"] = "yyyyMMdd HHmm"
        DATE_FORMAT_REGEXPS["^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$"] = "dd MMMM yyyy"
        DATE_FORMAT_REGEXPS["^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$"] = "dd MMM yyyy"
        DATE_FORMAT_REGEXPS["^\\d{4}/\\d{1,2}/\\d{1,2}$"] = "yyyy/MM/dd"
        DATE_FORMAT_REGEXPS["^\\d{1,2}/\\d{1,2}/\\d{4}$"] = "MM/dd/yyyy"
        DATE_FORMAT_REGEXPS["^\\d{4}.\\d{1,2}.\\d{1,2}$"] = "yyyy.MM.dd"
        DATE_FORMAT_REGEXPS["^\\d{4}-\\d{1,2}-\\d{1,2}$"] = "yyyy-MM-dd"
        DATE_FORMAT_REGEXPS["^\\d{1,2}.\\d{1,2}.\\d{4}$"] = "dd.MM.yyyy"
        DATE_FORMAT_REGEXPS["^\\d{1,2}-\\d{1,2}-\\d{4}$"] = "dd-MM-yyyy"
        DATE_FORMAT_REGEXPS["^\\d{4}$"] = "yyyy"
        DATE_FORMAT_REGEXPS["^\\d{2}$-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$"] = "yy-MM-dd HH:mm"
        DATE_FORMAT_REGEXPS["^\\d{2}$.\\d{1,2}.\\d{1,2}$"] = "yy.MM.dd"
        DATE_FORMAT_REGEXPS["^\\d{2}$-\\d{1,2}-\\d{1,2}$"] = "yy-MM-dd"
        DATE_FORMAT_REGEXPS["^\\d{2}$-\\d{1,2}"] = "yy-MM"
        DATE_FORMAT_REGEXPS["^\\d{4}$.\\d{1,2}"] = "yyyy-MM"
        DATE_FORMAT_REGEXPS["^\\d{4}$-\\d{1,2}"] = "yyyy-MM"
        DATE_FORMAT_REGEXPS["^\\d{1,2}.\\d{4}$"] = "MM-yyyy"
        DATE_FORMAT_REGEXPS["^\\d{1,2}-\\d{4}$"] = "MM-yyyy"
    }
}
