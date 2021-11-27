package com.dot.gonit.features.unitpercentage

import org.mariuszgromada.math.mxparser.Expression
import kotlin.math.roundToInt

object UnitAndPercentage {

    fun calculateUnit(text: String, toUnit: String): String? {
        return try {
            getRoundDecimalValue(Expression(text).calculate()) + " " + toUnit
        } catch (e: Exception) {
            null
        }
    }

    fun calculatePercentage(data: Triple<String, String, String>): String? {
        return when (data.second) {
            "of" -> {
                Expression(data.first + "*" + data.third).calculate().toString()
            }
            "off" -> {
                Expression(data.third + "-" + data.first + "*" + data.third).calculate().toString()
            }
            "on" -> {
                Expression(data.third + "+" + data.first + "*" + data.third).calculate().toString()
            }
            else -> null
        }
    }

    fun getRoundDecimalValue(number: Double): String? {
        return try {
            val number3digits: Double = ((number * 1000.0).roundToInt() / 1000.0)
            val number2digits: Double = ((number3digits * 100.0).roundToInt() / 100.0)
            ((number2digits * 10.0).roundToInt() / 10.0).toString()
        } catch (e: Exception) {
            null
        }
    }
}