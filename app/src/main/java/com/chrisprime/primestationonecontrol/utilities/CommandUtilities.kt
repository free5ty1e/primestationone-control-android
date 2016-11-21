package com.chrisprime.primestationonecontrol.utilities

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

import timber.log.Timber

/**
 * Created by cpaian on 7/20/15.
 */
object CommandUtilities {

    fun doCommand(command: List<String>): Int {
        var s: String? = null
        var processExitValue = 1   //Default to "errored"
        val processBuilder = ProcessBuilder(command)
        try {
            val process = processBuilder.start()

            val stdInput = BufferedReader(InputStreamReader(process.inputStream))
            val stdError = BufferedReader(InputStreamReader(process.errorStream))

            // read the output from the command
            Timber.d("Here is the standard output of the command:\n")
            s = stdInput.readLine()
            while (s != null) {
                Timber.d(s)
                s = stdInput.readLine()
            }

            // read any errors from the attempted command
            Timber.d("Here is the standard error of the command (if any):\n")
            s = stdError.readLine()
            while (s != null) {
                Timber.d(s)
                s = stdError.readLine()
            }

            processExitValue = process.waitFor()
        } catch (e: IOException) {
            Timber.e(e, "Exception executing command " + command)
        } catch (e: InterruptedException) {
            Timber.e(e, "Exception executing command, interrupted... " + command)
        }

        return processExitValue
    }
}
