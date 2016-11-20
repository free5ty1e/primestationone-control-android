package com.chrisprime.primestationonecontrol.model;

import android.content.Context;
import android.net.Uri;

import com.chrisprime.primestationonecontrol.utilities.FileUtilities;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by cpaian on 7/18/15.
 */
@Parcel
public class PrimeStationOne {
    public static final int DEFAULT_PI_SSH_PORT = 22;
    public static final String DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION = "/home/pi/primestationone/reference/txt/version.txt";
    public static final String SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME = "splashscreenwithcontrolsandversion.png";
    public static final String DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION = "/home/pi/" + SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME;
    public static final String PRIMESTATION_IMGUR_SPLASHSCREEN_SOURCE_IMAGE_URL = "http://i.imgur.com/UnMdAZX.png";
    public static final String PRIMESTATION_DATA_STORAGE_PREFIX = "ps1_";
    public static final String FOUND_PRIMESTATIONS_JSON_FILENAME = PRIMESTATION_DATA_STORAGE_PREFIX + "found_primestations.json";
    public static final String CURRENT_PRIMESTATION_JSON_FILENAME = PRIMESTATION_DATA_STORAGE_PREFIX + "current_primestation.json";

    @SerializedName("ipAddress")
    String ipAddress;
    @SerializedName("hostname")
    String hostname;
    @SerializedName("version")
    String version;
    @SerializedName("mac")
    String mac;
    @SerializedName("splashscreenUriString")
    String splashscreenUriString;
    @SerializedName("retrievedSplashscreen")
    boolean retrievedSplashscreen = false;
    @SerializedName("megaEmail")
    String megaEmail;
    @SerializedName("megaPassword")
    String megaPassword;

    public PrimeStationOne() {
    }

    public PrimeStationOne(String ipAddress, String hostname, String version, String mac) {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.version = version;
        this.mac = mac;
    }

    public void updateStoredPrimestation(Context context) {
        FileUtilities.storeCurrentPrimeStationToJson(context, this);
    }

    @Override
    public String toString() {
        return "PrimeStationOne{\n" +
                "ipAddress='" + ipAddress + '\'' +
                ", hostname='" + hostname + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isRetrievedSplashscreen() {
        return retrievedSplashscreen;
    }

    public void setRetrievedSplashscreen(boolean retrievedSplashscreen) {
        this.retrievedSplashscreen = retrievedSplashscreen;
    }

    public String getSplashscreenUriString() {
        return splashscreenUriString;
    }

    public Uri getSplashscreenUri() {
        return Uri.parse(splashscreenUriString);
    }

    public void setSplashscreenUriString(String splashscreenUri) {
        this.splashscreenUriString = splashscreenUri;
    }

    public void setSplashscreenUri(Uri splashscreenUri) {
        this.splashscreenUriString = splashscreenUri.toString();
    }

    public String getMegaEmail() {
        return megaEmail;
    }

    public void setMegaEmail(String megaEmail) {
        this.megaEmail = megaEmail;
    }

    public String getMegaPassword() {
        return megaPassword;
    }

    public void setMegaPassword(String megaPassword) {
        this.megaPassword = megaPassword;
    }
}
