package com.chrisprime.primestationonecontrol.utilities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.chrisprime.primestationonecontrol.BuildConfig;
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.dagger.Injector;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class PreferenceStore {

    @Inject
    SharedPreferences mPreferences;

    private static final PreferenceStore sInstance = new PreferenceStore();

    private PreferenceStore() {
        Injector.getApplicationComponent().inject(this);
    }

    /**
     * Gets the Singleton instance.
     *
     * @return
     */
    public static PreferenceStore getInstance() {
        return sInstance;
    }

    private Resources getResources() {
        return PrimeStationOneControlApplication.Companion.getAppResourcesContext().getResources();
    }

    /**
     * Retrieves a boolean value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(@StringRes int key, @BoolRes int defValue) {
        return getBoolean(key, getResources().getBoolean(defValue));
    }

    /**
     * Retrieves a boolean value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(@StringRes int key, boolean defValue) {
        return getBoolean(getResources().getString(key), defValue);
    }

    /**
     * Retrieves a boolean value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(@NonNull String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    /**
     * Saves a boolean value.
     *
     * @param key
     * @param value
     */
    public void putBoolean(@StringRes int key, @BoolRes int value) {
        putBoolean(key, getResources().getBoolean(value));
    }

    /**
     * Saves a boolean value.
     *
     * @param key
     * @param value
     */
    public void putBoolean(@StringRes int key, boolean value) {
        putBoolean(getResources().getString(key), value);
    }

    /**
     * Saves a boolean value.
     *
     * @param key
     * @param value
     */
    public void putBoolean(@NonNull String key, boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Retrieves an int value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(@StringRes int key, @IntegerRes int defValue) {
        return getInt(getResources().getString(key), getResources().getInteger(defValue));
    }

    /**
     * Retrieves an int value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(@NonNull String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    /**
     * Saves an int value.
     *
     * @param key
     * @param value
     */
    public void putInt(@StringRes int key, int value) {
        putInt(getResources().getString(key), value);
    }

    /**
     * Saves an int value.
     *
     * @param key
     * @param value
     */
    public void putInt(@NonNull String key, int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    /**
     * Retrieves a long value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(@StringRes int key, @IntegerRes int defValue) {
        return getLong(getResources().getString(key), getResources().getInteger(defValue));
    }


    public long getLong(@StringRes int key, long defValue) {
        return getLong(getResources().getString(key), defValue);
    }


    /**
     * Retrieves a long value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(@NonNull String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    /**
     * Saves a long value.
     *
     * @param key
     * @param value
     */
    public void putLong(@NonNull String key, long value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }


    /**
     * Saves a long value.
     *
     * @param key
     * @param value
     */
    public void putLong(@StringRes int key, long value) {
        putLong(getResources().getString(key), value);
    }

    /**
     * Retrieves an encrypted String value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(@StringRes int key, @StringRes int defValue) {
        return getString(key, getResources().getString(defValue));
    }

    /**
     * Retrieves an encrypted String value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(@StringRes int key, @Nullable String defValue) {
        return getString(getResources().getString(key), defValue);
    }

    /**
     * Retrieves an encrypted String value.
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(@NonNull String key, @Nullable String defValue) {
        return mPreferences.getString(key, defValue);
    }

    /**
     * Encrypts & saves a String value.
     *
     * @param key
     * @param value
     */
    public void putString(@StringRes int key, @StringRes int value) {
        putString(key, getResources().getString(value));
    }

    /**
     * Encrypts & saves a String value.
     *
     * @param key
     * @param value
     */
    public void putString(@StringRes int key, @Nullable String value) {
        putString(getResources().getString(key), value);
    }

    /**
     * Encrypts & saves a String value.
     *
     * @param key
     * @param value
     */
    public void putString(@NonNull String key, @Nullable String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    /**
     * Retrieves an encrypted GSON-annotated Object.
     *
     * @param key
     * @param defValue
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getObject(@StringRes int key, @Nullable T defValue, @NonNull Class<T> type) {
        return getObject(getResources().getString(key), defValue, type);
    }


    /**
     * Retrieves an encrypted GSON-annotated Object.
     *
     * @param key
     * @param defValue
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getObject(@NonNull String key, @Nullable T defValue, @NonNull Class<T> type) {
        String json = getString(key, null);
        if (BuildConfig.DEBUG) {
            Timber.d(".getObject(%s) retrieved json: %s", key, json);
        }
        return json == null ? defValue : ParsingUtilities.safeFromJson(json, type);
    }

    public <T> List<T> getObjectList(@StringRes int key, @Nullable List<T> defValue, @NonNull Class<T> type) {
        return getObjectList(getResources().getString(key), defValue, type);
    }

    public <T> List<T> getObjectList(@NonNull String key, @Nullable List<T> defValue, @NonNull Class<T> type) {
        String json = getString(key, null);
        if (BuildConfig.DEBUG) {
            Timber.d(".getObject(%s) retrieved json: %s", key, json);
        }
        return json == null ? defValue : ParsingUtilities.safeListFromJson(json, type);

    }

    /**
     * Encrypts & stores an GSON-annotated object.
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void putObject(@StringRes int key, @Nullable T value) {
        putObject(getResources().getString(key), value);
    }

    /**
     * Encrypts & stores an GSON-annotated object.
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void putObject(@NonNull String key, @Nullable T value) {
        putString(key, value == null ? null : ParsingUtilities.safeToJson(value));
    }


    /**
     * Encrypts & stores an GSON-annotated object.
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void putObjectList(@StringRes int key, @Nullable List<T> value, @NonNull Class<T> type) {
        putObjectList(getResources().getString(key), value, type);
    }

    /**
     * Encrypts & stores an GSON-annotated object.
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void putObjectList(@NonNull String key, @Nullable List<T> value, @NonNull Class<T> type) {
        putString(key, value == null ? null : ParsingUtilities.safeToJson(value, type));
    }

    /**
     * Removes an stored value.
     *
     * @param key
     */
    public void remove(@StringRes int key) {
        remove(getResources().getString(key));
    }

    /**
     * Removes an stored value.
     *
     * @param key
     */
    public void remove(@NonNull String key) {
        mPreferences.edit().remove(key).apply();
    }

    /**
     * Removes all stored values.
     */
    @SuppressLint("CommitPrefEdits")
    public void clear() {
        mPreferences.edit().clear().commit();
    }
}
