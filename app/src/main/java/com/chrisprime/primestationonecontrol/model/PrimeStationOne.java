package com.chrisprime.primestationonecontrol.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by cpaian on 7/18/15.
 */
@Parcel
public class PrimeStationOne {
    public static final String DEFAULT_PI_USERNAME = "pi";
    public static final String DEFAULT_PI_PASSWORD = "raspberry";
    public static final int DEFAULT_PI_SSH_PORT = 22;
    public static final String DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION = "/home/pi/primestationone/reference/txt/version.txt";
    public static final String SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME = "splashscreenwithcontrolsandversion.png";
    public static final String DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION = "/home/pi/" + SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME;
    public static final String PRIMESTATION_IMGUR_SPLASHSCREEN_SOURCE_IMAGE_URL = "http://i.imgur.com/UnMdAZX.png";
    public static final String PRIMESTATION_DATA_STORAGE_PREFIX = "ps1_";
    public static final String FOUND_PRIMESTATIONS_JSON_FILENAME = PRIMESTATION_DATA_STORAGE_PREFIX + "found_primestations.json";

    @SerializedName("ipAddress")
    String ipAddress;
    @SerializedName("hostname")
    String hostname;
    @SerializedName("version")
    String version;
    @SerializedName("mac")
    String mac;
    @SerializedName("splashscreenUri")
    Uri splashscreenUri;
    @SerializedName("retrievedSplashscreen")
    boolean retrievedSplashscreen = false;

    public PrimeStationOne() {
    }

    public PrimeStationOne(String ipAddress, String hostname, String version, String mac) {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.version = version;
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "PrimeStationOne{" +
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

    public Uri getSplashscreenUri() {
        return splashscreenUri;
    }

    public void setSplashscreenUri(Uri splashscreenUri) {
        this.splashscreenUri = splashscreenUri;
    }
}
