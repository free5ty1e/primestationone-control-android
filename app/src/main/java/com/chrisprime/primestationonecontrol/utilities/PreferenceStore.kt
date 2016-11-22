package com.chrisprime.primestationonecontrol.utilities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Resources
import android.support.annotation.BoolRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes

import com.chrisprime.primestationonecontrol.BuildConfig
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.dagger.Injector

import javax.inject.Inject

import timber.log.Timber

class PreferenceStore {

    @Inject
    lateinit var mPreferences: SharedPreferences

    init {
        Injector.applicationComponent.inject(this)
    }

    private val resources: Resources
        get() = PrimeStationOneControlApplication.appResourcesContext.resources

    /**
     * Retrieves a boolean value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getBoolean(@StringRes key: Int, @BoolRes defValue: Int): Boolean {
        return getBoolean(key, resources.getBoolean(defValue))
    }

    /**
     * Retrieves a boolean value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getBoolean(@StringRes key: Int, defValue: Boolean): Boolean {
        return getBoolean(resources.getString(key), defValue)
    }

    /**
     * Retrieves a boolean value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mPreferences.getBoolean(key, defValue)
    }

    /**
     * Saves a boolean value.

     * @param key
     * *
     * @param value
     */
    fun putBoolean(@StringRes key: Int, @BoolRes value: Int) {
        putBoolean(key, resources.getBoolean(value))
    }

    /**
     * Saves a boolean value.

     * @param key
     * *
     * @param value
     */
    fun putBoolean(@StringRes key: Int, value: Boolean) {
        putBoolean(resources.getString(key), value)
    }

    /**
     * Saves a boolean value.

     * @param key
     * *
     * @param value
     */
    fun putBoolean(key: String, value: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Retrieves an int value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getInt(@StringRes key: Int, @IntegerRes defValue: Int): Int {
        return getInt(resources.getString(key), resources.getInteger(defValue))
    }

    /**
     * Retrieves an int value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getInt(key: String, defValue: Int): Int {
        return mPreferences.getInt(key, defValue)
    }

    /**
     * Saves an int value.

     * @param key
     * *
     * @param value
     */
    fun putInt(@StringRes key: Int, value: Int) {
        putInt(resources.getString(key), value)
    }

    /**
     * Saves an int value.

     * @param key
     * *
     * @param value
     */
    fun putInt(key: String, value: Int) {
        val editor = mPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }


    /**
     * Retrieves a long value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getLong(@StringRes key: Int, @IntegerRes defValue: Int): Long {
        return getLong(resources.getString(key), resources.getInteger(defValue).toLong())
    }


    fun getLong(@StringRes key: Int, defValue: Long): Long {
        return getLong(resources.getString(key), defValue)
    }


    /**
     * Retrieves a long value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getLong(key: String, defValue: Long): Long {
        return mPreferences.getLong(key, defValue)
    }

    /**
     * Saves a long value.

     * @param key
     * *
     * @param value
     */
    fun putLong(key: String, value: Long) {
        val editor = mPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }


    /**
     * Saves a long value.

     * @param key
     * *
     * @param value
     */
    fun putLong(@StringRes key: Int, value: Long) {
        putLong(resources.getString(key), value)
    }

    /**
     * Retrieves an encrypted String value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getString(@StringRes key: Int, @StringRes defValue: Int): String {
        return getString(key, resources.getString(defValue))
    }

    /**
     * Retrieves an encrypted String value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getString(@StringRes key: Int, defValue: String?): String {
        return getString(resources.getString(key), defValue)!!
    }

    /**
     * Retrieves an encrypted String value.

     * @param key
     * *
     * @param defValue
     * *
     * @return
     */
    fun getString(key: String, defValue: String?): String? {
        return mPreferences.getString(key, defValue)
    }

    /**
     * Encrypts & saves a String value.

     * @param key
     * *
     * @param value
     */
    fun putString(@StringRes key: Int, @StringRes value: Int) {
        putString(key, resources.getString(value))
    }

    /**
     * Encrypts & saves a String value.

     * @param key
     * *
     * @param value
     */
    fun putString(@StringRes key: Int, value: String?) {
        putString(resources.getString(key), value)
    }

    /**
     * Encrypts & saves a String value.

     * @param key
     * *
     * @param value
     */
    fun putString(key: String, value: String?) {
        mPreferences.edit().putString(key, value).apply()
    }

    /**
     * Retrieves an encrypted GSON-annotated Object.

     * @param key
     * *
     * @param defValue
     * *
     * @param type
     * *
     * @param
     * *
     * @return
     */
    fun <T> getObject(@StringRes key: Int, defValue: T?, type: Class<T>): T {
        return getObject(resources.getString(key), defValue, type)
    }


    /**
     * Retrieves an encrypted GSON-annotated Object.

     * @param key
     * *
     * @param defValue
     * *
     * @param type
     * *
     * @param
     * *
     * @return
     */
    fun <T> getObject(key: String, defValue: T?, type: Class<T>): T {
        val json = getString(key, null)
        if (BuildConfig.DEBUG) {
            Timber.d(".getObject(%s) retrieved json: %s", key, json)
        }
        return if (json == null) defValue!! else ParsingUtilities.safeFromJson(json, type)!!
    }

    fun <T> getObjectList(@StringRes key: Int, defValue: List<T>?, type: Class<T>): List<T> {
        return getObjectList(resources.getString(key), defValue, type)
    }

    fun <T> getObjectList(key: String, defValue: List<T>?, type: Class<T>): List<T> {
        val json = getString(key, null)
        if (BuildConfig.DEBUG) {
            Timber.d(".getObject(%s) retrieved json: %s", key, json)
        }
        return if (json == null) defValue!! else ParsingUtilities.safeListFromJson(json, type)!!

    }

    /**
     * Encrypts & stores an GSON-annotated object.

     * @param key
     * *
     * @param value
     * *
     * @param
     */
    fun <T> putObject(@StringRes key: Int, value: T?) {
        putObject(resources.getString(key), value)
    }

    /**
     * Encrypts & stores an GSON-annotated object.

     * @param key
     * *
     * @param value
     * *
     * @param
     */
    fun <T> putObject(key: String, value: T?) {
        putString(key, if (value == null) null else ParsingUtilities.safeToJson(value))
    }


    /**
     * Encrypts & stores an GSON-annotated object.

     * @param key
     * *
     * @param value
     * *
     * @param
     */
    fun <T> putObjectList(@StringRes key: Int, value: List<T>?, type: Class<T>) {
        putObjectList(resources.getString(key), value, type)
    }

    /**
     * Encrypts & stores an GSON-annotated object.

     * @param key
     * *
     * @param value
     * *
     * @param
     */
    fun <T> putObjectList(key: String, value: List<T>?, type: Class<T>) {
        putString(key, if (value == null) null else ParsingUtilities.safeToJson(value, type))
    }

    /**
     * Removes an stored value.

     * @param key
     */
    fun remove(@StringRes key: Int) {
        remove(resources.getString(key))
    }

    /**
     * Removes an stored value.

     * @param key
     */
    fun remove(key: String) {
        mPreferences.edit().remove(key).apply()
    }

    /**
     * Removes all stored values.
     */
    @SuppressLint("CommitPrefEdits")
    fun clear() {
        mPreferences.edit().clear().commit()
    }

    companion object {

        /**
         * Gets the Singleton instance.

         * @return
         */
        val instance = PreferenceStore()
    }
}
