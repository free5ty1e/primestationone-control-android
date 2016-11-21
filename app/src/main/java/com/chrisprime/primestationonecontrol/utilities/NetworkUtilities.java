package com.chrisprime.primestationonecontrol.utilities;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
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
import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public class NetworkUtilities {

    //TODO: User preferences for timeouts
    public static final int PING_TIMEOUT_MILLIS = 150;
    public static final int SSH_TIMEOUT_MILLIS = 1500;

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

    public static String getPiUsername() {
        return PreferenceStore.getInstance().getString(R.string.pref_key_custom_pi_username, R.string.pref_default_custom_pi_username);
    }

    @NonNull
    public static List<String> getPiPasswordsToTry() {
        PreferenceStore preferenceStore = PreferenceStore.getInstance();
        String customPassword = preferenceStore.getString(R.string.pref_key_custom_pi_password, "");
        String[] passwords = PrimeStationOneControlApplication.getAppResourcesContext().getResources().getStringArray(R.array.array_passwords);
        List<String> passwordsToTry = new ArrayList<>();
        if (!TextUtils.isEmpty(customPassword)) {
            passwordsToTry.add(customPassword);
        }
        Collections.addAll(passwordsToTry, passwords);
        return passwordsToTry;
    }

    public static PrimeStationOne sshCheckForPi(String ip) {
        //First, if custom password is set, try with that
        //If that fails, try with each of the listed default passwords until one works or none do
        PrimeStationOne primeStationOne = null;
        for (String passwordToTry : getPiPasswordsToTry()) {
            String primestationVersionResponse = sshCheckForPi(ip, getPiUsername(), passwordToTry);
            if (!TextUtils.isEmpty(primestationVersionResponse)) {
                String hostname = getHostname(ip);
                String mac = "";
                primeStationOne = new PrimeStationOne(ip, hostname, primestationVersionResponse, mac, getPiUsername(), passwordToTry);
                Timber.d("Found PrimeStationOne: " + primeStationOne);
                break;
            }
        }
        return primeStationOne;
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

    public static Uri sshRetrieveAndSavePrimeStationFile(Context context, String ip, String user, String password,
                                                         int port, String fileLocationOnPrimestation, String fileNameToSaveLocally) {
        ChannelSftp channelSftp = connectSftpChannelToPi(ip, user, password, port);
        Uri uri = null;
        if (channelSftp != null) {
            File newFile;
            byte[] buffer = new byte[1024];
            BufferedInputStream bufferedInputStream;
            try {
                bufferedInputStream = new BufferedInputStream(channelSftp.get(fileLocationOnPrimestation));

                //Save splashscreen image under ip-based foldername
                File folder = FileUtilities.getPrimeStationStorageFolder(context, ip);

                newFile = new File(folder, fileNameToSaveLocally);
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
                    Timber.e(e, ".sshRetrieveAndSavePrimeStationFile() error: " + e.getMessage());
                }
            } catch (SftpException e) {
                Timber.e(e, ".sshRetrieveAndSavePrimeStationFile() error: " + e.getMessage());
            }
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

    public static String getHostname(String ipAddress) {
        String hostname = "hostname";
        InetAddress address;
        try {
            address = InetAddress.getByName(ipAddress);
            Timber.d("InetAddress for " + ipAddress + " = " + address);
            hostname = address.getCanonicalHostName();
            Timber.d("IP " + ipAddress + " hostname = " + hostname);
        } catch (Exception e) {
            Timber.e(e, "error obtaining hostname from " + ipAddress + ": " + e);
        }
        return hostname;
    }

    public interface SshCommandConsoleStdOutLineListener {
        void processConsoleStdOutLine(String line);
    }

    public static int sendSshCommandToPi(String ip, String user, String password, int port, String command,
                                         boolean waitForReturnValueAndCommandOutput,
                                         SshCommandConsoleStdOutLineListener sshCommandConsoleStdOutLineListener) {

        int exitStatus = -1;
        Session session = connectSshSessionToPi(ip, user, password, port);
        if (session != null) {
            Timber.d("Creating SSH command execution Channel...");
            try {
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);
                channel.setInputStream(null);

                ((ChannelExec) channel).setErrStream(System.err);
                InputStream in = channel.getInputStream();
                channel.connect();
                Timber.d("SSH command execution channel created and connected.");

                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0)
                            break;
                        String consoleOutputLine = new String(tmp, 0, i);
                        Timber.d(consoleOutputLine);
                        if (sshCommandConsoleStdOutLineListener != null) {
                            sshCommandConsoleStdOutLineListener.processConsoleStdOutLine(consoleOutputLine);
                        }
                    }
                    if (channel.isClosed() && waitForReturnValueAndCommandOutput) {
                        exitStatus = channel.getExitStatus();
                        Timber.d("exit-status: " + exitStatus);
                        break;
                    } else if (!waitForReturnValueAndCommandOutput) {    //Just return, dont care about exit status.
                        Timber.d("Just exiting without waiting for command exit status code...");
                        break;
                    }
                    Thread.sleep(1000);
                }
                channel.disconnect();
                session.disconnect();
            } catch (JSchException | IOException | InterruptedException e) {
                Timber.e(e, ".sendSshCommandToPi(" + ip + ") error: " + e + ": " + e.getMessage());
            }
        }
        return exitStatus;
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
