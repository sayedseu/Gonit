package com.dot.gonit.search

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import com.dot.gonit.features.currency.CurrencyManager.calculateCurrency
import com.dot.gonit.features.currency.CurrencyManager.convertCurrency
import com.dot.gonit.features.datetime.DateAndTimeManager.dateAndTime_ByDayName
import com.dot.gonit.features.datetime.DateAndTimeManager.dateAndTime_from_today
import com.dot.gonit.features.datetime.DateAndTimeManager.date_ByDayName
import com.dot.gonit.features.datetime.DateAndTimeManager.time_ByZoneId
import com.dot.gonit.features.datetime.DateAndTimeManager.time_in_ByZoneId
import com.dot.gonit.features.unitpercentage.UnitAndPercentage.getRoundDecimalValue
import com.dot.gonit.features.unitpercentage.UnitAndPercentageManager.average
import com.dot.gonit.features.unitpercentage.UnitAndPercentageManager.calculatePercentage
import com.dot.gonit.features.unitpercentage.UnitAndPercentageManager.calculateUnit
import com.dot.gonit.features.unitpercentage.UnitAndPercentageManager.sum
import org.mariuszgromada.math.mxparser.Expression

object SearchManager {
    private val operatorList = listOf(
        Pair("plus", "+"),
        Pair("and", "+"),
        Pair("with", "+"),
        Pair("subtract", "-"),
        Pair("minus", "-"),
        Pair("without", "-"),
        Pair("times", "*"),
        Pair("multiplied by", "*"),
        Pair("mul", "*"),
        Pair("divide by", "/"),
        Pair("divide", "/"),
        Pair(" ", "")
    )
    private val keywords = listOf(
        "in",
        "to",
        "as",
        "into",
        "time",
        "time in",
        "plus",
        "and",
        "with",
        "subtract",
        "minus",
        "without",
        "times",
        "multiplied by",
        "mul",
        "divide by",
        "of",
        "on",
        "off",
        "sum",
        "total",
        "avg",
        "average",
        "from",
        "max",
        "min"
    )
    private val pairList = listOf(
        Pair("\\d+\\s(USD|EUR|BC)\\s(to|in)\\s(USD|EUR|BC)", convertCurrency),
        Pair("(\\s*\\d+\\s(USD|EUR|BC)\\s*[\\+\\-\\*\\/]*)*", calculateCurrency),
        Pair("[A-Z]{1}(.)*\\s(time)\\s*", time_ByZoneId),
        Pair("(time in)\\s[A-Z]{1}(.)*", time_in_ByZoneId),
        Pair("\\d+\\s(weeks|months|years|days)\\s(from)\\s(today)\\s*", dateAndTime_from_today),
        Pair("\\d+\\s(minutes|hours)\\s(from)\\s(now)\\s*", dateAndTime_from_today),
        Pair("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)\\s*", date_ByDayName),
        Pair(
            "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)\\s(morning|afternoon|evening|night)\\s*",
            dateAndTime_ByDayName
        ),
        Pair("(avg|average)[(][a-z 0-9 ,]*[)]\\s*", average),
        Pair("(sum|total)[(][a-z 0-9 ,]*[)]\\s*", sum),
        Pair(
            "\\d+\\s*[*]\\s*[\\(][a-zA-Z/]*[\\)]\\s(to|in)\\s[\\(][a-zA-Z/]*[\\)]\\s*",
            calculateUnit
        ),
        Pair("\\d+[\\%]\\s(on|off|of)\\s\\d+\\s*", calculatePercentage)
    )

    fun doSearch(data: String): String? {
        var result: String? = null
        when {
            data.startsWith("//") -> {
                result = "//"
            }
            else -> {
                pairList.forEach {
                    if (it.first.toRegex().matches(data)) {
                        result = it.second(data)
                    }
                }
            }
        }
        if (result == null) {
            var replaceData = data
            operatorList.forEach {
                replaceData = replaceData.replace(it.first, it.second)
            }.let {
                result = try {
                    getRoundDecimalValue(Expression(replaceData).calculate())
                } catch (e: Exception) {
                    null
                }
            }
        }
        return result
    }

    fun setKeyword(editable: Editable?) {
        editable?.let {
            val text = it.toString()
            keywords.forEach { keyword ->
                val regex = Regex("\\b(?:${keyword})\\b")
                if (regex.containsMatchIn(text)) {
                    val start = text.indexOf(keyword)
                    val end = start + keyword.length
                    if (start >= 0) {
                        editable.setSpan(
                            ForegroundColorSpan(Color.parseColor("#49B6E8")), //this is
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }
    }
}