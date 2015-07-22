package com.chrisprime.primestationonecontrol.utilities;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;

import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class NetworkUtilities {
    //TODO: User preferences for timeouts
    public static final int PING_TIMEOUT_MILLIS = 150;
    public static final int SSH_TIMEOUT_MILLIS = 1500;

    //TODO: Default to full sweep but provide user settings for last octet range
    public static final int LAST_IP_OCTET_MIN = 1;
    public static final int LAST_IP_OCTET_MAX = 255;

    public static final String IP_SEPARATOR_CHAR_MATCHER = "\\.";
    public static final String IP_SEPARATOR_CHAR = ".";
    public static final String PING_RESPONSE_PREFIX_MATCHER = "from ";

    public static boolean ping(String ip) {
        List<String> commands = new ArrayList<>();
        commands.add("ping");
        commands.add("-c");
        commands.add("1");
        commands.add("-W");
        commands.add(String.valueOf(PING_TIMEOUT_MILLIS));
        commands.add(ip);
        int processExitValue = CommandUtilities.doCommand(commands);

        //Determine if ping of this address itself was successful
        return processExitValue == 0;
    }

    public static DhcpInfo getDhcpInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getDhcpInfo();
    }

    public static String sshCheckForPi(String ip) {
        return sshCheckForPi(ip, PrimeStationOne.DEFAULT_PI_USERNAME, PrimeStationOne.DEFAULT_PI_PASSWORD,
                PrimeStationOne.DEFAULT_PI_SSH_PORT, PrimeStationOne.DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION);
    }

    public static String sshCheckForPi(String ip, String user, String password) {
        return sshCheckForPi(ip, user, password, PrimeStationOne.DEFAULT_PI_SSH_PORT,
                PrimeStationOne.DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION);
    }

    public static String sshCheckForPi(String ip, String user, String password, int port, String remoteFile) {
        InputStream inputStream = getInputStreamFromPiRemoteFile(ip, user, password, port, remoteFile);
        return inputStream == null ? "" : readLastLineFromTextInputStream(inputStream);
    }

    public static String readLastLineFromTextInputStream(InputStream out) {
        String foundPi = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(out));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                foundPi = line;
                Timber.d(line);
            }
            br.close();
        } catch (IOException e) {
            Timber.e(e, ".readLastLineFromTextInputStream() error: " + e.getMessage());
        }
        return foundPi;
    }


    public static Uri sshRetrievePrimeStationImage(Context context, String ip, String user, String password, int port, String remoteFile) {
        ChannelSftp channelSftp = connectSftpChannelToPi(ip, user, password, port);
        File newFile = null;
        Uri uri = null;
        byte[] buffer = new byte[1024];
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(channelSftp.get(PrimeStationOne.DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION));

            //Save image file locally
            //TODO: Save splashscreen image under ip-based foldername
            newFile = new File(context.getFilesDir(), PrimeStationOne.SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME);

            try {
                OutputStream fileOutputStream = new FileOutputStream(newFile);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int readCount;
                while ((readCount = bufferedInputStream.read(buffer)) > 0) {
                    bufferedOutputStream.write(buffer, 0, readCount);
                }
                bufferedInputStream.close();
                bufferedOutputStream.close();
                uri = Uri.fromFile(newFile);
            } catch (IOException e) {
                Timber.e(e, ".sshRetrievePrimeStationImage() error: " + e.getMessage());
            }
        } catch (SftpException e) {
            Timber.e(e, ".sshRetrievePrimeStationImage() error: " + e.getMessage());
        }
        return uri;
    }

    public static InputStream getInputStreamFromPiRemoteFile(String ip, String user, String password, int port, String remoteFile) {
        ChannelSftp channelSftp = connectSftpChannelToPi(ip, user, password, port);
        InputStream inputStream = null;
        if (channelSftp != null) {
            try {
                inputStream = channelSftp.get(remoteFile);
            } catch (SftpException e) {
                Timber.e(e, ".getInputStreamFromPiRemoteFile(" + ip + ") error: " + e.getMessage());
            }
        }
        return inputStream;
    }

    public static ChannelSftp connectSftpChannelToPi(String ip, String user, String password, int port) {
        Session session = connectSshSessionToPi(ip, user, password, port);
        ChannelSftp channelSftp = null;
        if (session != null) {
            Timber.d("Creating SFTP Channel.");
            try {
                channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();
                Timber.d("SFTP Channel created.");
            } catch (JSchException e) {
                Timber.w(".connectSftpChannelToPi(" + ip + ") error: " + e + ": " + e.getMessage());
            }
        }
        return channelSftp;
    }

    public static Session connectSshSessionToPi(String ip, String user, String password, int port) {
        JSch jsch = new JSch();
        Session session;
        try {
            session = jsch.getSession(user, ip, port);
            session.setTimeout(SSH_TIMEOUT_MILLIS);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            Timber.d("Establishing Connection...");
            session.connect();
            Timber.d("Connection established.");
            return session;
        } catch (JSchException e) {
            Timber.w(".connectSshSessionToPi(" + ip + ") error: " + e + ": " + e.getMessage());
            return null;
        }
    }

    public static void putAddress(StringBuffer buf, int addr) {
        buf.append(intToInetAddress(addr).getHostAddress());
    }

    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     *
     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }
}
