package com.chrisprime.primestationonecontrol.model;

/**
 * Created by cpaian on 7/18/15.
 */
public class PrimeStationOne {
    private String ipAddress;
    private String hostname;
    private String version;

    public PrimeStationOne(String ipAddress, String hostname, String version) {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.version = version;
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
