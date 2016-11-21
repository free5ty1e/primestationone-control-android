/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

package com.chrisprime.primestationonecontrol.utilities

import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.util.regex.Matcher
import java.util.regex.Pattern

class RateControl {

    private val TAG = "RateControl"
    private val REACH_TIMEOUT = 5000
    private val CMD = "/system/bin/ping -A -q -n -w 3 -W 2 -c 3 "
    private val PTN = "^rtt min\\/avg\\/max\\/mdev = [0-9\\.]+\\/[0-9\\.]+\\/([0-9\\.]+)\\/[0-9\\.]+ ms.*"
    private val mPattern: Pattern
    private var line: String? = null
    var indicator: String? = null
    var rate = 800 // Slow start

    init {
        mPattern = Pattern.compile(PTN)
    }

    fun adaptRate() {
        var response_time = 0
        response_time = getAvgResponseTime(indicator!!)
        if ((response_time) > 0) {
            if (response_time > 100) { // Most distanced hosts
                rate = response_time * 5 // Minimum 500ms
            } else {
                rate = response_time * 10 // Maximum 1000ms
            }
            if (rate > REACH_TIMEOUT) {
                rate = REACH_TIMEOUT
            }
        }
    }

    private fun getAvgResponseTime(host: String): Int {
        // TODO: Reduce allocation
        var reader: BufferedReader? = null
        var matcher: Matcher
        try {
            val proc = Runtime.getRuntime().exec(CMD + host)
            reader = BufferedReader(InputStreamReader(proc.inputStream), BUF)
            line = reader.readLine()
            while (line != null) {
                matcher = mPattern.matcher(line!!)
                if (matcher.matches()) {
                    reader.close()
                    return java.lang.Float.parseFloat(matcher.group(1)).toInt()
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            Log.e(TAG, "Can't use native ping: " + e.message)
            try {
                val start = System.nanoTime()
                if (InetAddress.getByName(host).isReachable(REACH_TIMEOUT)) {
                    Log.i(TAG, "Using Java ICMP request instead ...")
                    return ((System.nanoTime() - start) / 1000).toInt()
                }
            } catch (e1: Exception) {
                Log.e(TAG, e1.message)
            }

        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (e: IOException) {
            }

        }
        return rate
    }

    companion object {
        private val BUF = 512
    }
}
