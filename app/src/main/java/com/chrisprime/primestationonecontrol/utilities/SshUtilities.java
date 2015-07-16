package com.chrisprime.primestationonecontrol.utilities;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class SshUtilities {
    public static String findPi() {
        String user = "pi";
        String password = "raspberry";
        String host = "192.168.1.53";
        int port = 22;
        String foundPi = "";

        String remoteFile = "/home/pi/primestationone/reference/txt/version.txt";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
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
                foundPi += line + "\n";
                Timber.d(line);
            }
            br.close();
        } catch (Exception e) {
            Timber.e(e.getMessage(), e);
        }
        return foundPi;
    }
}
