package com.chrisprime.primestationonecontrol.model

import android.content.Context
import com.chrisprime.primestationonecontrol.utilities.FileUtilities
import org.parceler.Parcel
import org.parceler.ParcelProperty

/**
 * Created by cpaian on 7/18/15.
 */
@Parcel(Parcel.Serialization.BEAN)
data class PrimeStationOne(
        @ParcelProperty("ipAddress") var ipAddress: String? = null,
        @ParcelProperty("hostname") var hostname: String? = null,
        @ParcelProperty("version") var version: String? = null,
        @ParcelProperty("mac") var mac: String? = null,
        @ParcelProperty("splashscreenUriString") var splashscreenUriString: String? = null,
        @ParcelProperty("retrievedSplashscreen") var isRetrievedSplashscreen: Boolean? = null,
        @ParcelProperty("megaEmail") var megaEmail: String? = null,
        @ParcelProperty("megaPassword") var megaPassword: String? = null,
        @ParcelProperty("piUser") var piUser: String? = null,
        @ParcelProperty("piPassword") var piPassword: String? = null) {

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
