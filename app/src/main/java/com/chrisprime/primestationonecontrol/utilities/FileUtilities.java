package com.chrisprime.primestationonecontrol.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cpaian on 7/23/15.
 */
public class FileUtilities {

    @NonNull
    public static File getPrimeStationStorageFolder(Context context, String ip) {
        if (ip == null) {
            ip = "";
        }
        File folder = new File(context.getFilesDir() + File.separator
                + PrimeStationOne.PRIMESTATION_DATA_STORAGE_PREFIX + ip);

        //Also, create this particular folder location in case it does not yet exist!
        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs();

        return folder;
    }

    public static void createAndSaveFile(Context context, String filename, String json) {
        try {
            Timber.d(".createAndSaveFile(" + filename + ")");
            File file = new File(getPrimeStationStorageFolder(context, null), filename);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Timber.e(e, ".createAndSaveFile(" + filename + ") failure: " + e);
        }
    }

    public static String readJsonData(File file) {
        String json = "";
        try {
            FileInputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer);
        } catch (IOException e) {
            Timber.w(e, ".readJsonData() failure: " + e);
        }
        return json;
    }

    @Nullable
    public static List<PrimeStationOne> readJsonPrimestationList(Context context) {
        String json = readJsonData(new File(getPrimeStationStorageFolder(context, null),
                PrimeStationOne.FOUND_PRIMESTATIONS_JSON_FILENAME));
        Timber.d("Read found primestations from JSON file:\n" + json);
        Type listType = new TypeToken<ArrayList<PrimeStationOne>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }
}
