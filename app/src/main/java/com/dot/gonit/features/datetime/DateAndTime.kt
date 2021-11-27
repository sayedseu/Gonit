package com.dot.gonit.features.datetime

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime

object DateAndTime {

    fun getTimeByZoneId(id: String): String? {
        return try {
            when {
                (id == "PST") -> {
                    DateTime(DateTimeZone.forID("PST8PDT")).toLocalTime().toString()
                }
                else -> {
                    DateTime(DateTimeZone.forID(id)).toLocalTime().toString()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDataAndTimeFromToday(amount: Int, name: String): String? {
        return try {
            when {
                (name == "weeks") -> {
                    getReadableDateTime(DateTime().plusWeeks(amount))
                }
                (name == "months") -> {
                    getReadableDateTime(DateTime().plusMonths(amount))
                }
                (name == "days") -> {
                    getReadableDateTime(DateTime().plusDays(amount))
                }
                (name == "years") -> {
                    getReadableDateTime(DateTime().plusYears(amount))
                }
                (name == "minutes") -> {
                    getReadableDateTime(DateTime().plusMinutes(amount))
                }
                (name == "hours") -> {
                    getReadableDateTime(DateTime().plusHours(amount))
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDayOfWeekByDayName(name: String): String? {
        val today = LocalDateTime().dayOfWeek
        val dateTime = LocalDateTime()
        return when (name) {
            "Monday" -> {
                return when {
                    today > DateTimeConstants.MONDAY -> {
                        dateTime.plusDays((DateTimeConstants.MONDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.MONDAY -> {
                        dateTime.plusDays((DateTimeConstants.MONDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            "Tuesday" -> {
                return when {
                    today > DateTimeConstants.TUESDAY -> {
                        dateTime.plusDays((DateTimeConstants.TUESDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.TUESDAY -> {
                        dateTime.plusDays((DateTimeConstants.TUESDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            "Wednesday" -> {
                return when {
                    today > DateTimeConstants.WEDNESDAY -> {
                        dateTime.plusDays((DateTimeConstants.WEDNESDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.WEDNESDAY -> {
                        dateTime.plusDays((DateTimeConstants.WEDNESDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            "Thursday" -> {
                return when {
                    today > DateTimeConstants.THURSDAY -> {
                        dateTime.plusDays((DateTimeConstants.THURSDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.THURSDAY -> {
                        dateTime.plusDays((DateTimeConstants.THURSDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            "Friday" -> {
                return when {
                    today > DateTimeConstants.FRIDAY -> {
                        dateTime.plusDays((DateTimeConstants.FRIDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.FRIDAY -> {
                        dateTime.plusDays((DateTimeConstants.FRIDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            "Saturday" -> {
                return when {
                    today > DateTimeConstants.SATURDAY -> {
                        dateTime.plusDays((DateTimeConstants.SATURDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.SATURDAY -> {
                        dateTime.plusDays((DateTimeConstants.SATURDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            "Sunday" -> {
                return when {
                    today > DateTimeConstants.SUNDAY -> {
                        dateTime.plusDays((DateTimeConstants.SUNDAY - today) + 7).toLocalDate()
                            .toString()
                    }
                    today < DateTimeConstants.SUNDAY -> {
                        dateTime.plusDays((DateTimeConstants.SUNDAY - today)).toLocalDate()
                            .toString()
                    }
                    else -> {
                        dateTime.plusDays(7).toLocalDate().toString()
                    }
                }
            }
            else -> null

        }
    }

    fun getDayOfWeekWithTimeByDayName(day: String, time: String): String? {
        return when (time) {
            "morning" -> {
                getDayOfWeekByDayName(day) + "  08:00"
            }
            "afternoon" -> {
                getDayOfWeekByDayName(day) + "  14:00"
            }
            "evening" -> {
                getDayOfWeekByDayName(day) + "  19:00"
            }
            "night" -> {
                getDayOfWeekByDayName(day) + "  21:00"
            }
            else -> null
        }
    }

    private fun getReadableDateTime(dateTime: DateTime): String {
        val date = dateTime.toLocalDate()
        return dateTime.hourOfDay.toString() + ":" + dateTime.minuteOfHour.toString() + "  " + date.toString()
    }
}