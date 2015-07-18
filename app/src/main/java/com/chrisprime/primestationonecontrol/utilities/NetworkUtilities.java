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

import timber.log.Timber;

public class NetworkUtilities {

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
            Timber.e(e.getMessage(), e);
        }
        return foundPi;
    }


}
