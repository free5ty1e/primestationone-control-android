package com.chrisprime.primestationonecontrol.utilities

import android.content.Context

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.events.PrimeStationsListUpdatedEvent
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.util.ArrayList

import timber.log.Timber

/**
 * Created by cpaian on 7/23/15.
 */
object FileUtilities {

    fun getPrimeStationStorageFolder(context: Context, ip: String?): File {
        var ipNonNull = ip
        if (ip == null) {
            ipNonNull = ""
        }
        val folder = File(context.filesDir.toString() + File.separator
                + PrimeStationOne.PRIMESTATION_DATA_STORAGE_PREFIX + ipNonNull)

        //Also, create this particular folder location in case it does not yet exist!
        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs()

        return folder
    }

    fun createAndSaveFile(context: Context, filename: String, json: String) {
        try {
            Timber.d(".createAndSaveFile($filename)")
            val file = File(getPrimeStationStorageFolder(context, null), filename)
            val fileWriter = FileWriter(file)
            fileWriter.write(json)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            Timber.e(e, ".createAndSaveFile($filename) failure: $e")
        }

    }

    fun readJsonData(file: File): String {
        var json = ""
        try {
            val `is` = FileInputStream(file)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer)
        } catch (e: IOException) {
            Timber.w(e, ".readJsonData() failure: " + e)
        }

        return json
    }

    fun readJsonPrimestationList(context: Context): List<PrimeStationOne>? {
        val json = readJsonData(File(getPrimeStationStorageFolder(context, null),
                PrimeStationOne.FOUND_PRIMESTATIONS_JSON_FILENAME))
        Timber.d("Read found primestations from JSON file:\n" + json)
        val listType = object : TypeToken<ArrayList<PrimeStationOne>>() {

        }.type
        return Gson().fromJson<List<PrimeStationOne>>(json, listType)
    }

    fun readJsonCurrentPrimestation(context: Context): PrimeStationOne? {
        val json = readJsonData(File(getPrimeStationStorageFolder(context, null),
                PrimeStationOne.CURRENT_PRIMESTATION_JSON_FILENAME))
        Timber.d("Read current primestation from JSON file:\n" + json)
        return Gson().fromJson(json, PrimeStationOne::class.java)
    }

    fun storeFoundPrimeStationsJson(context: Context, primeStationOneList: List<PrimeStationOne>) {
        //Store found primestations as JSON file
        val jsonString = Gson().toJson(primeStationOneList)
        Timber.d("bundled found primestations into JSON string:\n" + jsonString)
        createAndSaveFile(context, PrimeStationOne.FOUND_PRIMESTATIONS_JSON_FILENAME, jsonString)
        PrimeStationOneControlApplication.eventBus.post(PrimeStationsListUpdatedEvent())
    }

    fun storeCurrentPrimeStationToJson(context: Context, primeStationOne: PrimeStationOne) {
        //Store current primestation as JSON file
        val jsonString = Gson().toJson(primeStationOne)
        Timber.d("bundled current primestation into JSON string:\n" + jsonString)
        createAndSaveFile(context, PrimeStationOne.CURRENT_PRIMESTATION_JSON_FILENAME, jsonString)

        //ALSO find current primestation in json list of found primestations, and update its info in case user switches to another primestation
        val primeStationOneList: MutableList<PrimeStationOne>? = readJsonPrimestationList(context)?.toMutableList()
        if (primeStationOneList == null) {
            Timber.w(".storeCurrentPrimeStationToJson(): PrimeStationOne List is null, cannot proceed!")
        } else {
            if (primeStationOneList.size > 0) {
                for (i in 0..primeStationOneList.size - 1) {
                    var ps1 = primeStationOneList[i]
                    if (primeStationOne.ipAddress == ps1.ipAddress) {
                        primeStationOneList[i] = primeStationOne
                    }
                }
            }
            storeFoundPrimeStationsJson(context, primeStationOneList)
        }
    }
}
