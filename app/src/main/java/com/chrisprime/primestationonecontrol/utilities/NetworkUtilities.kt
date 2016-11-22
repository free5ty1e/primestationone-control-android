package com.chrisprime.primestationonecontrol.utilities

import android.content.Context
import android.net.DhcpInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.text.TextUtils

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.ArrayList
import java.util.Collections

import timber.log.Timber


object NetworkUtilities {

    //TODO: User preferences for timeouts
    @JvmStatic val PING_TIMEOUT_MILLIS = 150
    @JvmStatic val SSH_TIMEOUT_MILLIS = 1500

    @JvmStatic val IP_SEPARATOR_CHAR_MATCHER = "\\."
    @JvmStatic val IP_SEPARATOR_CHAR = "."
    @JvmStatic val PING_RESPONSE_PREFIX_MATCHER = "from "

    @JvmStatic fun ping(ip: String): Boolean {
        val commands = ArrayList<String>()
        commands.add("ping")
        commands.add("-c")
        commands.add("1")
        commands.add("-W")
        commands.add(PING_TIMEOUT_MILLIS.toString())
        commands.add(ip)
        val processExitValue = CommandUtilities.doCommand(commands)

        //Determine if ping of this address itself was successful
        return processExitValue == 0
    }

    @JvmStatic fun getDhcpInfo(context: Context): DhcpInfo {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.dhcpInfo
    }

    @JvmStatic val piUsername: String
        get() = PreferenceStore.instance.getString(R.string.pref_key_custom_pi_username, R.string.pref_default_custom_pi_username)

    @JvmStatic val piPasswordsToTry: List<String>
        get() {
            val preferenceStore = PreferenceStore.instance
            val customPassword = preferenceStore.getString(R.string.pref_key_custom_pi_password, "")
            val passwords = PrimeStationOneControlApplication.appResourcesContext.resources.getStringArray(R.array.array_passwords)
            val passwordsToTry = ArrayList<String>()
            if (!TextUtils.isEmpty(customPassword)) {
                passwordsToTry.add(customPassword)
            }
            Collections.addAll(passwordsToTry, *passwords)
            return passwordsToTry
        }

    @JvmStatic fun sshCheckForPi(ip: String): PrimeStationOne? {
        //First, if custom password is set, try with that
        //If that fails, try with each of the listed default passwords until one works or none do
        var primeStationOne: PrimeStationOne? = null
        for (passwordToTry in piPasswordsToTry) {
            val primestationVersionResponse = sshCheckForPi(ip, piUsername, passwordToTry)
            if (!TextUtils.isEmpty(primestationVersionResponse)) {
                val hostname = getHostname(ip)
                val mac = ""
                Timber.d("Trying to log into %s with %s/%s...", ip, piUsername, passwordToTry)
                primeStationOne = PrimeStationOne(ip, hostname, primestationVersionResponse!!, mac, piUsername, passwordToTry)
                Timber.d("Found PrimeStationOne: " + primeStationOne)
                break
            }
        }
        return primeStationOne
    }

    @JvmOverloads @JvmStatic fun sshCheckForPi(ip: String, user: String, password: String, port: Int = PrimeStationOne.DEFAULT_PI_SSH_PORT, remoteFile: String = PrimeStationOne.DEFAULT_PRIMESTATION_VERSION_TEXT_FILE_LOCATION): String? {
        val inputStream = getInputStreamFromPiRemoteFile(ip, user, password, port, remoteFile)
        return if (inputStream == null) "" else readLastLineFromTextInputStream(inputStream)
    }

    @JvmStatic fun readLastLineFromTextInputStream(inputStream: InputStream): String? {
        Timber.d(".readLastLineFromTextInputStream()...")
        var foundPi: String? = null
        val br = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        try {
            line = br.readLine()
            while (!TextUtils.isEmpty(line)) {
                foundPi = line
                Timber.d(".readLastLineFromTextInputStream(): line read! --> %s", line)
                line = br.readLine()
            }
            br.close()
        } catch (e: IOException) {
            Timber.e(e, ".readLastLineFromTextInputStream() error: " + e.message)
        }
        Timber.d(".readLastLineFromTextInputStream(): finished reading file, last line being returned! --> %s", foundPi)
        return foundPi
    }

    @JvmStatic fun sshRetrieveAndSavePrimeStationFile(context: Context, ip: String, user: String, password: String,
                                           port: Int, fileLocationOnPrimestation: String, fileNameToSaveLocally: String): Uri? {
        val channelSftp = connectSftpChannelToPi(ip, user, password, port)
        var uri: Uri? = null
        if (channelSftp != null) {
            val newFile: File
            val buffer = ByteArray(1024)
            val bufferedInputStream: BufferedInputStream
            try {
                bufferedInputStream = BufferedInputStream(channelSftp.get(fileLocationOnPrimestation))

                //Save splashscreen image under ip-based foldername
                val folder = FileUtilities.getPrimeStationStorageFolder(context, ip)

                newFile = File(folder, fileNameToSaveLocally)
                try {
                    val fileOutputStream = FileOutputStream(newFile)
                    val bufferedOutputStream = BufferedOutputStream(fileOutputStream)
                    var readCount: Int = bufferedInputStream.read(buffer)
                    while (readCount > 0) {
                        bufferedOutputStream.write(buffer, 0, readCount)
                        readCount = bufferedInputStream.read(buffer)
                    }
                    bufferedInputStream.close()
                    bufferedOutputStream.close()
                    uri = Uri.fromFile(newFile)
                } catch (e: IOException) {
                    Timber.e(e, ".sshRetrieveAndSavePrimeStationFile() error: " + e.message)
                }

            } catch (e: SftpException) {
                Timber.e(e, ".sshRetrieveAndSavePrimeStationFile() error: " + e.message)
            }

        }
        return uri
    }

    @JvmStatic fun getInputStreamFromPiRemoteFile(ip: String, user: String, password: String, port: Int, remoteFile: String): InputStream? {
        val channelSftp = connectSftpChannelToPi(ip, user, password, port)
        var inputStream: InputStream? = null
        if (channelSftp != null) {
            try {
                inputStream = channelSftp.get(remoteFile)
            } catch (e: SftpException) {
                Timber.e(e, ".getInputStreamFromPiRemoteFile(" + ip + ") error: " + e.message)
            }

        }
        return inputStream
    }

    @JvmStatic fun connectSftpChannelToPi(ip: String, user: String, password: String, port: Int): ChannelSftp? {
        val session = connectSshSessionToPi(ip, user, password, port)
        var channelSftp: ChannelSftp? = null
        if (session != null) {
            Timber.d("Creating SFTP Channel.")
            try {
                channelSftp = session.openChannel("sftp") as ChannelSftp
                channelSftp.connect()
                Timber.d("SFTP Channel created.")
            } catch (e: JSchException) {
                Timber.w(".connectSftpChannelToPi(" + ip + ") error: " + e + ": " + e.message)
            }

        }
        return channelSftp
    }

    @JvmStatic fun getHostname(ipAddress: String): String {
        var hostname = "hostname"
        val address: InetAddress
        try {
            address = InetAddress.getByName(ipAddress)
            Timber.d("InetAddress for $ipAddress = $address")
            hostname = address.canonicalHostName
            Timber.d("IP $ipAddress hostname = $hostname")
        } catch (e: Exception) {
            Timber.e(e, "error obtaining hostname from $ipAddress: $e")
        }

        return hostname
    }


    @JvmStatic fun sendSshCommandToPi(ip: String, user: String, password: String, port: Int, command: String,
                           waitForReturnValueAndCommandOutput: Boolean,
                           sshCommandConsoleStdOutLineListener: SshCommandConsoleStdOutLineListener?): Int {

        var exitStatus = -1
        val session = connectSshSessionToPi(ip, user, password, port)
        if (session != null) {
            Timber.d("Creating SSH command execution Channel...")
            try {
                val channel = session.openChannel("exec")
                (channel as ChannelExec).setCommand(command)
                channel.setInputStream(null)

                channel.setErrStream(System.err)
                val `in` = channel.getInputStream()
                channel.connect()
                Timber.d("SSH command execution channel created and connected.")

                val tmp = ByteArray(1024)
                while (true) {
                    while (`in`.available() > 0) {
                        val i = `in`.read(tmp, 0, 1024)
                        if (i < 0)
                            break
                        val consoleOutputLine = String(tmp, 0, i)
                        Timber.d(consoleOutputLine)
                        sshCommandConsoleStdOutLineListener?.processConsoleStdOutLine(consoleOutputLine)
                    }
                    if (channel.isClosed() && waitForReturnValueAndCommandOutput) {
                        exitStatus = channel.getExitStatus()
                        Timber.d("exit-status: " + exitStatus)
                        break
                    } else if (!waitForReturnValueAndCommandOutput) {    //Just return, dont care about exit status.
                        Timber.d("Just exiting without waiting for command exit status code...")
                        break
                    }
                    Thread.sleep(1000)
                }
                channel.disconnect()
                session.disconnect()
            } catch (e: JSchException) {
                Timber.e(e, ".sendSshCommandToPi(" + ip + ") error: " + e + ": " + e.message)
            } catch (e: IOException) {
                Timber.e(e, ".sendSshCommandToPi(" + ip + ") error: " + e + ": " + e.message)
            } catch (e: InterruptedException) {
                Timber.e(e, ".sendSshCommandToPi(" + ip + ") error: " + e + ": " + e.message)
            }

        }
        return exitStatus
    }

    @JvmStatic fun connectSshSessionToPi(ip: String, user: String, password: String, port: Int): Session? {
        val jsch = JSch()
        val session: Session
        try {
            session = jsch.getSession(user, ip, port)
            session.timeout = SSH_TIMEOUT_MILLIS
            session.setPassword(password)
            session.setConfig("StrictHostKeyChecking", "no")
            Timber.d("Establishing Connection...")
            session.connect()
            Timber.d("Connection established.")
            return session
        } catch (e: JSchException) {
            Timber.w(".connectSshSessionToPi(" + ip + ") error: " + e + ": " + e.message)
            return null
        }

    }

    @JvmStatic fun putAddress(buf: StringBuffer, addr: Int) {
        buf.append(intToInetAddress(addr).hostAddress)
    }

    /**
     * Convert a IPv4 address from an integer to an InetAddress.

     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    @JvmStatic fun intToInetAddress(hostAddress: Int): InetAddress {
        val addressBytes = byteArrayOf((0xff and hostAddress).toByte(), (0xff and (hostAddress shr 8)).toByte(), (0xff and (hostAddress shr 16)).toByte(), (0xff and (hostAddress shr 24)).toByte())

        try {
            return InetAddress.getByAddress(addressBytes)
        } catch (e: UnknownHostException) {
            throw AssertionError()
        }

    }
}
