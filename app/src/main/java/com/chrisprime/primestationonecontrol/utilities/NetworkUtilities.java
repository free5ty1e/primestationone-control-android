package com.chrisprime.primestationonecontrol.utilities;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import timber.log.Timber;

public class NetworkUtilities {

    //TODO: User preference for timeouts
    public static final int TIMEOUT_MILLIS = 1500;
    public static final String IP_SEPARATOR_CHAR_MATCHER = "\\.";
    public static final String IP_SEPARATOR_CHAR = ".";
    //TODO: Uncomment for full sweep!
/*
    private static final int LAST_IP_OCTET_MIN = 1;
    private static final int LAST_IP_OCTET_MAX = 255;
*/
    //TODO: Default to full sweep but provide user settings for last octet range
    public static final int LAST_IP_OCTET_MIN = 50;
    public static final int LAST_IP_OCTET_MAX = 55;

    public static DhcpInfo getDhcpInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getDhcpInfo();
    }

    public static String sshCheckForPi(String ip) {
        return sshCheckForPi(ip, "pi", "raspberry", 22, "/home/pi/primestationone/reference/txt/version.txt");
    }

    public static String sshCheckForPi(String ip, String user, String password) {
        return sshCheckForPi(ip, user, password, 22, "/home/pi/primestationone/reference/txt/version.txt");
    }

    public static String sshCheckForPi(String ip, String user, String password, int port, String remoteFile) {
        String foundPi = "";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, ip, port);
            session.setTimeout(TIMEOUT_MILLIS);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            Timber.d("Establishing Connection...");
            session.connect();
            Timber.d("Connection established.");
            Timber.d("Crating SFTP Channel.");
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            Timber.d("SFTP Channel created.");

            InputStream out = null;
            out = sftpChannel.get(remoteFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;
            while ((line = br.readLine()) != null) {
                foundPi = line;
                Timber.d(line);
            }
            br.close();
        } catch (Exception e) {
            Timber.e(ip + " error: " + e.getMessage(), e);
        } finally {

        }

        return foundPi;
    }


    public static void putAddress(StringBuffer buf, int addr) {
        buf.append(intToInetAddress(addr).getHostAddress());
    }

    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }
}
