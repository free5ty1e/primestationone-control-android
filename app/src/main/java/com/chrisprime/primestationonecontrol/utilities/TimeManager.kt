package com.chrisprime.primestationonecontrol.utilities

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Handle getting the time.  It will look at the local time and
 * determine if it should be trusted based on the last request
 * to the feed service.
 */
class TimeManager {

    companion object {
        val instance = TimeManager()
    }

    val ONE_WEEK_IN_MILLIS = 604800000L
    val ONE_DAY_IN_MILLIS = 86400000L
    val FIFTEEN_MINS_IN_MILLIS = 900000L
    val ONE_HOUR_IN_MILLIS = 3600000L
    val ONE_MINUTE_IN_MILLIS = 60000L
    val ONE_SECOND_IN_MILLIS = 1000L


    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun currentTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis()
        return calendar
    }

    fun getMinuteSecondDifference(earlier: Calendar, later: Calendar): IntArray {
        val differences = IntArray(3)
        val diff = later.timeInMillis - earlier.timeInMillis
        differences[0] = TimeUnit.MILLISECONDS.toHours(diff).toInt()
        differences[1] = TimeUnit.MILLISECONDS.toMinutes(diff).toInt() % 60
        differences[2] = (TimeUnit.MILLISECONDS.toSeconds(diff) % 60 + 1).toInt()

        return differences
    }

    fun timeFrameIsWithinDate(milliToAdd: Long, daystoAdd: Int): Boolean {
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, daystoAdd)
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0)
        val startOfDay = c.timeInMillis
        val timeFrame = currentTimeMillis() + milliToAdd
        val endOfDay = startOfDay + ONE_DAY_IN_MILLIS
        return timeFrame > startOfDay && timeFrame < endOfDay
    }
}
