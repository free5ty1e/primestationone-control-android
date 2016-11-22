package com.chrisprime.primestationonecontrol.utilities

import android.net.Uri

import com.bluelinelabs.logansquare.LoganSquare

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.HashMap

import timber.log.Timber

/**
 * Created by cpaian on 8/4/15.
 */
object ParsingUtilities {

    fun appendQueryParameterWithNoEncoding(uri: Uri, parameter: String, value: String): Uri {
        val appendQuestionMark = if (uri.toString().contains("?")) "&" else "?"
        return Uri.parse(uri.toString() + appendQuestionMark + parameter + "=" + value)
    }

    fun urlEncodeSpacesOnly(url: String): String {
        return url.replace(" ", "%20")
    }

    fun <T> safeFromJson(json: String, classOfT: Class<T>): T? {
        try {
            return LoganSquare.parse(json, classOfT)
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }

    fun <T> safeListFromJson(json: String, classOfT: Class<T>): ArrayList<T>? {
        try {
            return LoganSquare.parseList(json, classOfT) as ArrayList<T>
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }


    fun <T> safeMapFromJson(json: String, classOfT: Class<T>): HashMap<String, T>? {
        try {
            return LoganSquare.parseMap(json, classOfT) as HashMap<String, T>
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }


    fun <T> safeMapFromJson(`in`: InputStream, classOfT: Class<T>): HashMap<String, T>? {
        try {
            return LoganSquare.parseMap(`in`, classOfT) as HashMap<String, T>
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }

    fun safeToJson(`object`: Any): String? {
        try {
            return LoganSquare.serialize(`object`)
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }


    fun <T> safeToJson(objects: List<T>, classOfT: Class<T>): String? {
        try {
            return LoganSquare.serialize(objects, classOfT)
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }

    fun <T> safeToJson(objects: Array<T>, classOfT: Class<T>): String? {
        try {
            return LoganSquare.serialize(Arrays.asList(*objects), classOfT)
        } catch (e: IOException) {
            Timber.e(e, e.message)
        }

        return null
    }

    fun registerLoganSquareTypeConverters() {
        LoganSquare.registerTypeConverter(Calendar::class.java, CalendarTypeConverter())
    }

    fun dateToCalendar(date: Date?): Calendar {
        var valid: Calendar? = null
        if (date != null) {
            valid = Calendar.getInstance()
            valid!!.time = date
        }
        return valid!!
    }
}
