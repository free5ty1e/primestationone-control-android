package com.chrisprime.primestationonecontrol.utilities

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

import timber.log.Timber

class CalendarTypeConverter : StringBasedTypeConverter<Calendar>() {
    override fun getFromString(string: String?): Calendar? {

        if (string == null) {
            return null
        }

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")

        val calendar = Calendar.getInstance()
        try {
            calendar.time = format.parse(string)
        } catch (e: ParseException) {
            Timber.e(e, "Failed to parse Calendar. Error: %s", e.message)
            return null
        }

        return calendar
    }

    override fun convertToString(`object`: Calendar): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(`object`.time)
    }
}
