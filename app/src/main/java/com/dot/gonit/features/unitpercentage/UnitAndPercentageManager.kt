package com.dot.gonit.features.unitpercentage

object UnitAndPercentageManager {

    val average: (String) -> String? = { "avg" }

    val sum: (String) -> String? = { "sum" }

    val calculateUnit: (String) -> String? = {
        val result: String?
        result = try {
            var replaceString = it.replace("to", "/")
            replaceString = replaceString.replace("in", "/")
            replaceString = replaceString.replace("(", "[")
            replaceString = replaceString.replace(")", "]")
            var toUnit =
                replaceString.split("\\d+\\s*[*]\\s*[\\[][a-zA-Z/]*[\\]]\\s[\\/]\\s".toRegex())[1]
            toUnit = toUnit.replace("[", "")
            toUnit = toUnit.replace("]", "")
            UnitAndPercentage.calculateUnit(replaceString, toUnit)
        } catch (e: Exception) {
            null
        }
        result
    }

    val calculatePercentage: (String) -> String? = {
        val result: String?
        result = try {
            val split = it.split("\\s".toRegex())
            UnitAndPercentage.calculatePercentage(Triple(split[0], split[1], split[2]))
        } catch (e: Exception) {
            null
        }
        result
    }
}