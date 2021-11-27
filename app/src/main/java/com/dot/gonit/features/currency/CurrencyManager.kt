package com.dot.gonit.features.currency

object CurrencyManager {

    val convertCurrency: (String) -> String? = { it ->
        val finalList = mutableListOf<String>()
        val split = it.split("(to|in)".toRegex())
        split.firstOrNull()?.let { value ->
            val secondSplit = value.trim().split("\\s".toRegex())
            secondSplit.firstOrNull()?.let { finalList.add(0, it) }
            secondSplit.lastOrNull()?.let { finalList.add(1, it) }
        }
        split.lastOrNull()?.let { finalList.add(2, it) }
        Currency.convertCurrency(
            finalList.component1().trim().toDouble(),
            finalList.component2().trim(),
            finalList.component3().trim()
        )
    }

    val calculateCurrency: (String) -> String? = {
        var result: String? = null
        val operatorList = mutableListOf<String>()
        val valueList = mutableListOf<String>()
        val dataList = mutableListOf<Pair<String, String>>()
        try {
            it.split("\\d+\\s(USD|EUR|BC)".toRegex()).forEach { value ->
                if (value.isNotBlank()) {
                    operatorList.add(value.trim())
                }
            }
            it.split("[-+*/]".toRegex()).forEach { value ->
                if (value.isNotBlank()) {
                    valueList.add(value.trim())
                }
            }
            valueList.forEach { value ->
                val split = value.split("\\s".toRegex())
                dataList.add(Pair(split.first(), split.last()))
            }
            if (operatorList.isNotEmpty() && valueList.isNotEmpty() && operatorList.size < valueList.size) {
                result = Currency.calculateCurrency(dataList, operatorList)
            }

        } catch (e: Exception) {
            result = null
        }
        result
    }

}