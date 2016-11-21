/*
 * Copyright (C) 2009-2010 Aubort Jean-Baptiste (Rorist)
 * Licensed under GNU's GPL 2, see README
 */

//am start -a android.intent.action.MAIN -n com.android.settings/.wifi.WifiSettings
package com.chrisprime.primestationonecontrol.utilities

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import java.util.regex.Matcher
import java.util.regex.Pattern

// TODO: IPv6 support

class NetInfo(private val ctxt: Context) {
    private val TAG = "NetInfo"
    private var info: WifiInfo? = null
    private val prefs: SharedPreferences

    var intf = "eth0"
    var ip = NOIP
    var cidr = 24

    var speed = 0
    var ssid: String? = null
    var bssid: String? = null
    var carrier: String? = null
    var macAddress = NOMAC
    var netmaskIp = NOMASK
    var gatewayIp = NOIP

    init {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        getIp()
        wifiInfo

        // Set ARP enabled
        // try {
        // Runtime.getRuntime().exec("su -C ip link set dev " + intf +
        // " arp on");
        // } catch (Exception e) {
        // Log.e(TAG, e.getMessage());
        // }
        // Runtime.getRuntime().exec("echo 1 > /proc/sys/net/ipv4/conf/" + intf
        // + "/proxy_arp");
        // Runtime.getRuntime().exec("echo 1 > /proc/sys/net/ipv4/conf/tun0/proxy_arp");
    }

    override fun hashCode(): Int {
        val ip_custom = if (prefs.getBoolean(KEY_IP_CUSTOM, DEFAULT_IP_CUSTOM)) 1 else 0
        val ip_start = prefs.getString(KEY_IP_START, DEFAULT_IP_START)!!.hashCode()
        val ip_end = prefs.getString(KEY_IP_END, DEFAULT_IP_END)!!.hashCode()
        val cidr_custom = if (prefs.getBoolean(KEY_CIDR_CUSTOM, DEFAULT_CIDR_CUSTOM)) 1 else 0
        val cidr = prefs.getString(KEY_CIDR, DEFAULT_CIDR)!!.hashCode()
        return 42 + intf.hashCode() + ip.hashCode() + cidr + ip_custom + ip_start + ip_end + cidr_custom + cidr
    }

    fun getIp() {
        intf = prefs.getString(KEY_INTF, DEFAULT_INTF)
        try {
            if (intf === DEFAULT_INTF || NOIF == intf) {
                // Automatic interface selection
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val ni = en.nextElement()
                    intf = ni.name
                    ip = getInterfaceFirstIp(ni)
                    if (ip !== NOIP) {
                        break
                    }
                }
            } else {
                // Defined interface from CannedPrefsActivity
                ip = getInterfaceFirstIp(NetworkInterface.getByName(intf))
            }
        } catch (e: SocketException) {
            Log.e(TAG, e.message)
            //Editor edit = sPreferences.edit();
            //edit.putString(KEY_INTF, DEFAULT_INTF);
            //edit.commit();
        }

        getCidr()
    }

    private fun getInterfaceFirstIp(ni: NetworkInterface?): String {
        if (ni != null) {
            val nis = ni.inetAddresses
            while (nis.hasMoreElements()) {
                val ia = nis.nextElement()
                if (!ia.isLoopbackAddress) {
                    if (ia is Inet6Address) {
                        Log.i(TAG, "IPv6 detected and not supported yet!")
                        continue
                    }
                    return ia.hostAddress
                }
            }
        }
        return NOIP
    }

    private fun getCidr() {
        if (netmaskIp !== NOMASK) {
            cidr = IpToCidr(netmaskIp)
        } else {
            var match: String?
            // Running ip tools
            try {
                match = runCommand("/system/xbin/ip", String.format(CMD_IP, intf), String.format(PTN_IP1, intf))
                if (match != null) {
                    cidr = Integer.parseInt(match)
                    return
                } else {
                    match = runCommand("/system/xbin/ip", String.format(CMD_IP, intf), String.format(PTN_IP2, intf))
                    if (match != null) {
                        cidr = Integer.parseInt(match)
                        return
                    } else {
                        match = runCommand("/system/bin/ifconfig", " " + intf, String.format(PTN_IF, intf))
                        if (match != null) {
                            cidr = IpToCidr(match)
                            return
                        } else {
                            Log.i(TAG, "cannot find cidr, using default /24")
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                Log.i(TAG, e.message + " -> cannot find cidr, using default /24")
            }

        }
    }

    // FIXME: Factorize, this isn't a generic runCommand()
    private fun runCommand(path: String, cmd: String, ptn: String): String? {
        try {
            if (File(path).exists() == true) {
                var line: String?
                var matcher: Matcher
                val ptrn = Pattern.compile(ptn)
                val p = Runtime.getRuntime().exec(path + cmd)
                val r = BufferedReader(InputStreamReader(p.inputStream), BUF)
                line = r.readLine()
                while (line != null) {
                    matcher = ptrn.matcher(line)
                    if (matcher.matches()) {
                        return matcher.group(1)
                    }
                    line = r.readLine()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't use native command: " + e.message)
            return null
        }

        return null
    }

    @Suppress("SENSELESS_COMPARISON")
    val mobileInfo: Boolean
        get() {
            val tm = ctxt.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (tm != null) {
                carrier = tm.networkOperatorName
            }
            return false
        }

    @Suppress("SENSELESS_COMPARISON")
            // Set wifi variables
    // broadcastIp = getIpFromIntSigned((dhcp.ipAddress & dhcp.netmask)
    // | ~dhcp.netmask);
    val wifiInfo: Boolean
        get() {
            val wifi = ctxt.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifi != null) {
                info = wifi.connectionInfo
                speed = info!!.linkSpeed
                ssid = info!!.ssid
                bssid = info!!.bssid
                macAddress = info!!.macAddress
                gatewayIp = getIpFromIntSigned(wifi.dhcpInfo.gateway)
                netmaskIp = getIpFromIntSigned(wifi.dhcpInfo.netmask)
                return true
            }
            return false
        }

    val netIp: String
        get() {
            val shift = 32 - cidr
            val start = getUnsignedLongFromIp(ip).toInt() shr shift shl shift
            return getIpFromLongUnsigned(start.toLong())
        }

    /*
     * public String getIp() { return getIpFromIntSigned(dhcp.ipAddress); }
     * public int getNetCidr() { int i = dhcp.netmask; i = i - ((i >> 1) &
     * 0x55555555); i = (i & 0x33333333) + ((i >> 2) & 0x33333333); return ((i +
     * (i >> 4) & 0xF0F0F0F) * 0x1010101) >> 24; // return 24; } public String
     * getNetIp() { return getIpFromIntSigned(dhcp.ipAddress & dhcp.netmask); }
     */
    // public String getNetmask() {
    // return getIpFromIntSigned(dhcp.netmask);
    // }

    // public String getBroadcastIp() {
    // return getIpFromIntSigned((dhcp.ipAddress & dhcp.netmask) |
    // ~dhcp.netmask);
    // }

    // public Object getGatewayIp() {
    // return getIpFromIntSigned(dhcp.gateway);
    // }

    val supplicantState: SupplicantState
        get() = info!!.supplicantState

    private fun IpToCidr(ip: String): Int {
        var sum = -2.0
        val part = ip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (p in part) {
            sum += 256.0 - java.lang.Double.parseDouble(p)
        }
        return 32 - (Math.log(sum) / Math.log(2.0)).toInt()
    }

    companion object {

        val KEY_INTF = "interface"
        val DEFAULT_INTF: String? = null

        val KEY_IP_START = "ip_start"
        val DEFAULT_IP_START = "0.0.0.0"

        val KEY_IP_END = "ip_end"
        val DEFAULT_IP_END = "0.0.0.0"

        val KEY_IP_CUSTOM = "ip_custom"
        val DEFAULT_IP_CUSTOM = false

        val KEY_CIDR_CUSTOM = "cidr_custom"
        val DEFAULT_CIDR_CUSTOM = false

        val KEY_CIDR = "cidr"
        val DEFAULT_CIDR = "24"

        private val BUF = 8 * 1024
        private val CMD_IP = " -f inet addr show %s"
        private val PTN_IP1 = "\\s*inet [0-9\\.]+\\/([0-9]+) brd [0-9\\.]+ scope global %s$"
        private val PTN_IP2 = "\\s*inet [0-9\\.]+ peer [0-9\\.]+\\/([0-9]+) scope global %s$" // FIXME: Merge with PTN_IP1
        private val PTN_IF = "^%s: ip [0-9\\.]+ mask ([0-9\\.]+) flags.*"
        private val NOIF = "0"
        val NOIP = "0.0.0.0"
        val NOMASK = "255.255.255.255"
        val NOMAC = "00:00:00:00:00:00"

        fun isConnected(ctxt: Context): Boolean {
            val nfo = (ctxt.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (nfo != null) {
                return nfo.isConnected
            }
            return false
        }

        fun getUnsignedLongFromIp(ip_addr: String): Long {
            val a = ip_addr.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return (Integer.parseInt(a[0]) * 16777216 + Integer.parseInt(a[1]) * 65536
                    + Integer.parseInt(a[2]) * 256 + Integer.parseInt(a[3])).toLong()
        }

        fun getIpFromIntSigned(ip_int: Int): String {
            var ip = ""
            for (k in 0..3) {
                ip = ip + (ip_int shr k * 8 and 0xFF) + "."
            }
            return ip.substring(0, ip.length - 1)
        }

        fun getIpFromLongUnsigned(ip_long: Long): String {
            var ip = ""
            for (k in 3 downTo -1 + 1) {
                ip = ip + (ip_long shr k * 8 and 0xFF) + "."
            }
            return ip.substring(0, ip.length - 1)
        }
    }

    // public int getIntFromInet(InetAddress ip_addr) {
    // return getIntFromIp(ip_addr.getHostAddress());
    // }

    // private InetAddress getInetFromInt(int ip_int) {
    // byte[] quads = new byte[4];
    // for (int k = 0; k < 4; k++)
    // quads[k] = (byte) ((ip_int >> k * 8) & 0xFF); // 0xFF=255
    // try {
    // return InetAddress.getByAddress(quads);
    // } catch (java.net.UnknownHostException e) {
    // return null;
    // }
    // }
}
