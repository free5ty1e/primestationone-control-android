package com.chrisprime.primestationonecontrol.utilities

import android.content.Context
import android.content.res.Resources
import android.support.annotation.StringRes
import android.support.v4.util.TimeUtils

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.R

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A semi-sophisticated time formatter, that formats time in human relative terms.
 */
class TimeFormatter(internal val mResources: Resources) {

    /**
     * Formats the specified time.  The placeholder %1$s in the specified resource will be replaced
     * by the month and day (for future and past days) or the time (for today).
     *
     *
     * If the time is some date in the future then it will use the
     * format specified by resourceIdForFutureDay (example "Launches on Nov 27").
     *
     *
     * If the time is today and the specified time is in the future it will use the
     * resourceIdForTodayInFuture (example "Launches at 5:00pm).
     *
     *
     * If the time is today and the specified time is in the past it will use the
     * resourceIfForTodayInPast (example "Launched at 5:00pm" or "CLOSED")
     *
     *
     * If the time is some date in the past then it will use the format specified
     * by resourceIdForPastDay (example "Launched on Nov 27")
     *
     *
     * If now is equal to the milliseconds to the specified time then it will use the
     * resourceIdForTodayInFuture
     *
     *
     * Please note this uses the TimeManagers time to determine relative time.

     * @param resourceIdForFutureDay     resource to use if date in future
     * *
     * @param resourceIdForTodayInFuture resource to use if time in future and it is today
     * *
     * @param resourceIdForTodayInPast   resource to use if time is in the past and it is today
     * *
     * @param resourceIdForPastDay       resource to use if the date is in the past
     * *
     * @param time                       the time to format
     */

    fun formatAsHumanReadable(@StringRes resourceIdForFutureDay: Int,
                              @StringRes resourceIdForTodayInFuture: Int,
                              @StringRes resourceIdForTodayInPast: Int,
                              @StringRes resourceIdForPastDay: Int, time: Long): String {
        val results: String

        val nowInMs = TimeManager.instance.currentTimeMillis()
        val now = Calendar.getInstance()
        now.timeInMillis = nowInMs
        now.timeZone = TimeZone.getDefault()  //use the user's time zone, not UTC to figure out what is "now"

        val importantDateAndTime = Calendar.getInstance()
        importantDateAndTime.timeInMillis = time
        importantDateAndTime.timeZone = TimeZone.getDefault()


        if (now.get(Calendar.YEAR) == importantDateAndTime.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_YEAR) == importantDateAndTime.get(Calendar.DAY_OF_YEAR)) {
            //it is today
            val formatter = SimpleDateFormat("h:mm a", Locale.US)
            if (time >= nowInMs) {
                // future & today
                results = String.format(mResources.getString(resourceIdForTodayInFuture), formatter.format(time))
            } else {
                // today in the past
                results = String.format(mResources.getString(resourceIdForTodayInPast), formatter.format(time))
            }
        } else if (time >= nowInMs) {
            //future & not today
            val formatter = SimpleDateFormat("MMM dd", Locale.US)
            results = String.format(mResources.getString(resourceIdForFutureDay), formatter.format(time))
        } else { //(time < nowInMs)
            // that is the past & not today
            val formatter = SimpleDateFormat("MMM dd", Locale.US)
            results = String.format(mResources.getString(resourceIdForPastDay), formatter.format(time))
        }

        return results
    }

    companion object {

        fun getFriendlyTime(periodInMilliseconds: Long, inPast: Boolean): String {
            val appResourcesContext = PrimeStationOneControlApplication.instance
            val stringBuilder = StringBuilder()
            var remainingTimeDifference = periodInMilliseconds / 1000

            val seconds = if (remainingTimeDifference >= 60) remainingTimeDifference % 60 else remainingTimeDifference
            remainingTimeDifference = Math.ceil(remainingTimeDifference.toDouble() / 60).toLong()
            val minutes = if (remainingTimeDifference >= 60) remainingTimeDifference % 60 else remainingTimeDifference
            remainingTimeDifference /= 60
            val hours = if (remainingTimeDifference >= 24) remainingTimeDifference % 24 else remainingTimeDifference
            remainingTimeDifference /= 24
            val days = if (remainingTimeDifference >= 30) remainingTimeDifference % 30 else remainingTimeDifference
            remainingTimeDifference /= 30
            val months = if (remainingTimeDifference >= 12) remainingTimeDifference % 12 else remainingTimeDifference
            val years = remainingTimeDifference / 12

            if (years > 0) {
                stringBuilder.append(appResourcesContext.resources.getQuantityString(R.plurals.plural_years, years.toInt(), years.toInt()))
            } else if (months > 0) {
                stringBuilder.append(appResourcesContext.resources.getQuantityString(R.plurals.plural_months, months.toInt(), months.toInt()))
            } else if (days > 0) {
                stringBuilder.append(appResourcesContext.resources.getQuantityString(R.plurals.plural_days, days.toInt(), days.toInt()))
            } else if (hours > 0) {
                stringBuilder.append(appResourcesContext.resources.getQuantityString(R.plurals.plural_hours, hours.toInt(), hours.toInt()))
            } else if (minutes > 0) {
                stringBuilder.append(appResourcesContext.resources.getQuantityString(R.plurals.plural_minutes, minutes.toInt(), minutes.toInt()))
            } else {
                stringBuilder.append(appResourcesContext.resources.getQuantityString(R.plurals.plural_seconds, seconds.toInt(), seconds.toInt()))
            }

            if (inPast) {
                stringBuilder.append(appResourcesContext.getString(R.string.friendly_time_ago))
            }
            return stringBuilder.toString()
        }

        fun getPrettyPrintTime(calendar: Calendar?): String? {
            return format("yyyy-MM-dd HH:mm:ss", calendar, false)
        }

        fun getTwelveHourTime(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("h:mma", Locale.US)
            return dateFormat.format(Date(timeInMillis))
        }

        fun getDurationString(durationInMillis: Long): String {
            // Just for debugging; not internationalized.
            val sb = StringBuilder()
            TimeUtils.formatDuration(durationInMillis, sb)
            return sb.toString()
        }

        fun format(@StringRes templateRes: Int, calendar: Calendar?, lowerCaseAmPm: Boolean): String? {
            return format(PrimeStationOneControlApplication.instance.getString(templateRes), calendar, lowerCaseAmPm)
        }

        fun format(template: String, calendar: Calendar?, lowerCaseAmPm: Boolean): String? {
            if (calendar == null) {
                return null
            }
            val formatter = SimpleDateFormat(template, Locale.US)
            if (lowerCaseAmPm) {
                // force lowercase AM/PM (no way to do it via format)
                val symbols = formatter.dateFormatSymbols
                symbols.amPmStrings = arrayOf("am", "pm")
                formatter.dateFormatSymbols = symbols
            }
            return formatter.format(calendar.time)
        }
    }
}
