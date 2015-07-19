package com.chrisprime.primestationonecontrol.model;

/**
 * Created by cpaian on 7/18/15.
 */
public class PrimeStationOne {
    public static final String DEFAULT_PI_USERNAME = "pi";
    public static final String DEFAULT_PI_PASSWORD = "raspberry";
    public static final int DEFAULT_PI_SSH_PORT = 22;
    public static final String DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION = "/home/pi/primestationone/reference/txt/version.txt";
    public static final String SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME = "splashscreenwithcontrolsandversion.png";
    public static final String DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION = "/home/pi/" + SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME;

    private String ipAddress;
    private String hostname;
    private String version;

    public PrimeStationOne(String ipAddress, String hostname, String version) {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.version = version;
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
}
