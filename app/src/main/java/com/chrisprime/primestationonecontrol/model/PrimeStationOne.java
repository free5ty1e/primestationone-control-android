package com.chrisprime.primestationonecontrol.model;

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

    @SerializedName("ipAddress")
    String ipAddress;
    @SerializedName("hostname")
    String hostname;
    @SerializedName("version")
    String version;
    @SerializedName("mac")
    String mac;

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
}
