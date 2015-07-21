package com.chrisprime.primestationonecontrol.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cpaian on 7/20/15.
 */
public class CommandUtilities {

    public static int doCommand(List<String> command)
    {
        String s = null;
        int processExitValue = 1;   //Default to "errored"
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // read the output from the command
            Timber.d("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                Timber.d(s);
            }

            // read any errors from the attempted command
            Timber.d("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                Timber.d(s);
            }

            processExitValue = process.waitFor();
        } catch(IOException e) {
            Timber.e(e, "Exception executing command " + command);
        } catch (InterruptedException e) {
            Timber.e(e, "Exception executing command, interrupted... " + command);
        }
        return processExitValue;
    }
}
