/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

// Inspired by http://connectbot.googlecode.com/svn/trunk/connectbot/src/org/connectbot/bean/HostBean.java
package com.chrisprime.primestationonecontrol.utilities

import com.chrisprime.primestationonecontrol.BuildConfig
import java.util.*

@org.parceler.Parcel
class HostBean {

    var deviceType = TYPE_COMPUTER
    var isAlive = 1
    var position = 0
    var responseTime = 0 // ms
    var ipAddress: String? = null
    var hostname: String? = null
    var hardwareAddress = NetInfo.NOMAC
    var nicVendor = "Unknown"
    var os = "Unknown"
    var services: HashMap<Int, String>? = null
    var banners: HashMap<Int, String>? = null
    var portsOpen: ArrayList<Int>? = null
    var portsClosed: ArrayList<Int>? = null

    companion object {

        val PKG = BuildConfig.APPLICATION_ID

        val EXTRA = PKG + ".extra"
        val EXTRA_POSITION = PKG + ".extra_position"
        val EXTRA_HOST = PKG + ".extra_host"
        val EXTRA_TIMEOUT = PKG + ".network.extra_timeout"
        val EXTRA_HOSTNAME = PKG + ".extra_hostname"
        val EXTRA_BANNERS = PKG + ".extra_banners"
        val EXTRA_PORTSO = PKG + ".extra_ports_o"
        val EXTRA_PORTSC = PKG + ".extra_ports_c"
        val EXTRA_SERVICES = PKG + ".extra_services"
        val TYPE_GATEWAY = 0
        val TYPE_COMPUTER = 1

    }
}
