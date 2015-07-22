package com.chrisprime.primestationonecontrol.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cpaian on 7/20/15.
 */
public class CommandUtilities {

    public static String doCommand(List<String> command)
    {
        String s = null;
        String commandResult = "";
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process process = pb.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder stringBuilder = new StringBuilder();

            // read the output from the command
            String stdOutTitle = "Here is the standard output of the command:\n";
            Timber.d(stdOutTitle);
            stringBuilder.append(stdOutTitle);
            while ((s = stdInput.readLine()) != null) {
                Timber.d(s);
                stringBuilder.append(s);
            }

            // read any errors from the attempted command
            String stdErrTitle = "Here is the standard error of the command (if any):\n";
            Timber.d(stdErrTitle);
            stringBuilder.append(stdErrTitle);
            while ((s = stdError.readLine()) != null) {
                Timber.d(s);
                stringBuilder.append(s);
            }
            commandResult = stringBuilder.toString();
            process.exitValue()
        } catch(IOException e) {
            Timber.e(e, "Exception executing command " + command);
        }
        return commandResult;
    }
}
