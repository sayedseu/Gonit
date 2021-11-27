package com.dot.gonit.features.operators

import android.view.View
import android.widget.TextView
import com.dot.gonit.R

object Operators {

    fun calculateAvg(
        text: String,
        resultTextView: TextView,
        firstTextView: TextView,
        viewList: List<View>
    ) {
        try {
            val list = mutableListOf<Int>()
            var replaceText = text.replace("avg", "")
            replaceText = replaceText.replace("average", "")
            replaceText = replaceText.replace("(", "")
            replaceText = replaceText.replace(")", "")
            replaceText.split("[,]".toRegex()).forEach {
                val s = it.replace("l", "").trim()
                if (s.isNotBlank() && ("\\d+".toRegex().matches(s))) {
                    list.add(s.toInt())
                }
            }.let {
                var total = 0.0
                val regex = Regex("\\d+(?:\\.\\d+)?")
                list.forEach {
                    if (it == 1) {
                        regex.find(firstTextView.text)?.let { value ->
                            total += value.value.toDouble()
                        }
                    } else if (it > 1) {
                        val resultView: TextView = viewList[it - 2].findViewById(R.id.resultTV)
                        regex.find(resultView.text)?.let { value ->
                            total += value.value.toDouble()
                        }
                    }
                }.let {
                    val finaleResult = (total / list.size).toString()
                    resultTextView.text = finaleResult
                }
            }
        } catch (e: Exception) {
        }
    }

    fun calculateSum(
        text: String,
        resultTextView: TextView,
        firstTextView: TextView,
        viewList: List<View>
    ) {
        try {
            val list = mutableListOf<Int>()
            var replaceText = text.replace("sum", "")
            replaceText = replaceText.replace("total", "")
            replaceText = replaceText.replace("(", "")
            replaceText = replaceText.replace(")", "")
            replaceText.split("[,]".toRegex()).forEach {
                val s = it.replace("l", "").trim()
                if (s.isNotBlank() && ("\\d+".toRegex().matches(s))) {
                    list.add(s.toInt())
                }
            }.let {
                var total = 0.0
                val regex = Regex("\\d+(?:\\.\\d+)?")
                list.forEach {
                    if (it == 1) {
                        regex.find(firstTextView.text)?.let { value ->
                            total += value.value.toDouble()
                        }
                    } else if (it > 1) {
                        val resultView: TextView = viewList[it - 2].findViewById(R.id.resultTV)
                        regex.find(resultView.text)?.let { value ->
                            total += value.value.toDouble()
                        }
                    }
                }.let {
                    val finaleResult = total.toString()
                    resultTextView.text = finaleResult
                }
            }
        } catch (e: Exception) {
        }
    }
}