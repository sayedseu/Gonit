package com.dot.gonit.features.currency

import com.dot.gonit.features.unitpercentage.UnitAndPercentage.getRoundDecimalValue
import org.mariuszgromada.math.mxparser.Expression

object Currency {

    fun convertCurrency(amount: Double, from: String, to: String): String? {
        return if (from == to) {
            "$amount $from"
        } else {
            val measureCurrency = measureCurrency(amount, from, to)
            if (measureCurrency == null) null
            else "$measureCurrency $to"
        }
    }

    fun calculateCurrency(data: List<Pair<String, String>>, operator: List<String>): String? {
        val state = data[0].second
        val builder = StringBuilder()
        return try {
            for ((index, value) in data.withIndex()) {
                measureCurrency(value.first.toDouble(), state, value.second)?.let {
                    if (index < operator.size) {
                        builder.append(it).append(operator[index])
                    } else {
                        builder.append(it)
                    }
                }
            }
            Expression(builder.toString()).calculate().toString() + " " + state
        } catch (e: Exception) {
            null
        }
    }

    private fun measureCurrency(amount: Double, from: String, to: String): String? {
        return if (from == to) {
            amount.toString()
        } else if (from == "USD" && to == "EUR") {
            getRoundDecimalValue(amount.times(0.81))
        } else if (from == "EUR" && to == "USD") {
            getRoundDecimalValue(amount.times(1.23))
        } else if (from == "BC" && to == "USD") {
            getRoundDecimalValue(amount.times(35321.30))
        } else if (from == "BC" && to == "EUR") {
            getRoundDecimalValue(amount.times(29051.77))
        } else if (from == "USD" && to == "BC") {
            getRoundDecimalValue(amount.times(0.000028))
        } else if (from == "EUR" && to == "BC") {
            getRoundDecimalValue(amount.times(0.000034))
        } else {
            null
        }
    }

}