package com.dot.gonit.features.datetime

object DateAndTimeManager {

    val time_ByZoneId: (String) -> String? = {
        val result: String?
        result = try {
            val s = it.split("\\s(time)\\s*".toRegex())[0].trim()
            DateAndTime.getTimeByZoneId(s)
        } catch (e: Exception) {
            null
        }
        result
    }

    val time_in_ByZoneId: (String) -> String? = {
        val result: String?
        result = try {
            val s = it.split("(time in)\\s".toRegex())[1].trim()
            DateAndTime.getTimeByZoneId(s)
        } catch (e: Exception) {
            null
        }
        result
    }

    val dateAndTime_from_today: (String) -> String? = {
        val result: String?
        result = try {
            val split = it.split("\\s".toRegex())
            val amount = split[0]
            val name = split[1]
            DateAndTime.getDataAndTimeFromToday(amount.toInt(), name)
        } catch (e: Exception) {
            null
        }
        result
    }

    val date_ByDayName: (String) -> String? = {
        DateAndTime.getDayOfWeekByDayName(it.trim())
    }

    val dateAndTime_ByDayName: (String) -> String? = {
        val result: String?
        result = try {
            val split = it.split("\\s".toRegex())
            DateAndTime.getDayOfWeekWithTimeByDayName(split[0].trim(), split[1].trim())
        } catch (e: Exception) {
            null
        }
        result
    }
}