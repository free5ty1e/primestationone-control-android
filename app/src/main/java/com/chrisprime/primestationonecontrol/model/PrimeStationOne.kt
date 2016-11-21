package com.chrisprime.primestationonecontrol.model

import android.content.Context
import android.net.Uri

import com.chrisprime.primestationonecontrol.utilities.FileUtilities
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

import org.parceler.Parcel

/**
 * Created by cpaian on 7/18/15.
 */
@Parcel
class PrimeStationOne {

    @SerializedName("ipAddress")
    var ipAddress: String
    @SerializedName("hostname")
    var hostname: String
    @SerializedName("version")
    var version: String
    @SerializedName("mac")
    var mac: String
    @SerializedName("splashscreenUriString")
    var splashscreenUriString: String? = null
    @SerializedName("retrievedSplashscreen")
    var isRetrievedSplashscreen = false
    @SerializedName("megaEmail")
    var megaEmail: String? = null
    @SerializedName("megaPassword")
    var megaPassword: String? = null
    @SerializedName("piUser")
    var piUser: String
    @SerializedName("piPassword")
    var piPassword: String

    constructor(ipAddress: String, hostname: String, version: String, mac: String, piUser: String, piPassword: String) {
        this.ipAddress = ipAddress
        this.hostname = hostname
        this.version = version
        this.mac = mac
        this.piUser = piUser
        this.piPassword = piPassword
    }

    fun updateStoredPrimestation(context: Context) {
        FileUtilities.storeCurrentPrimeStationToJson(context, this)
    }

    override fun toString(): String {
        return "PrimeStationOne{" +
                "ipAddress='" + ipAddress + '\'' +
                ", hostname='" + hostname + '\'' +
                ", version='" + version + '\'' +
                ", mac='" + mac + '\'' +
                ", splashscreenUriString='" + splashscreenUriString + '\'' +
                ", retrievedSplashscreen=" + isRetrievedSplashscreen +
                ", megaEmail='" + megaEmail + '\'' +
                ", megaPassword='" + megaPassword + '\'' +
                ", piUser='" + piUser + '\'' +
                ", piPassword='" + piPassword + '\'' +
                '}'
    }

    var splashscreenUri: Uri
        get() = Uri.parse(splashscreenUriString)
        set(splashscreenUri) {
            this.splashscreenUriString = splashscreenUri.toString()
        }

    companion object {
        val DEFAULT_PI_SSH_PORT = 22
        val DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION = "/home/pi/primestationone/reference/txt/version.txt"
        val SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME = "splashscreenwithcontrolsandversion.png"
        val DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION = "/home/pi/" + SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME
        val PRIMESTATION_IMGUR_SPLASHSCREEN_SOURCE_IMAGE_URL = "http://i.imgur.com/UnMdAZX.png"
        val PRIMESTATION_DATA_STORAGE_PREFIX = "ps1_"
        val FOUND_PRIMESTATIONS_JSON_FILENAME = PRIMESTATION_DATA_STORAGE_PREFIX + "found_primestations.json"
        val CURRENT_PRIMESTATION_JSON_FILENAME = PRIMESTATION_DATA_STORAGE_PREFIX + "current_primestation.json"
    }
}
