package com.chrisprime.primestationonecontrol.utilities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cpaian on 8/4/15.
 */
public class ParsingUtilities {

    public static Uri appendQueryParameterWithNoEncoding(Uri uri, String parameter, String value) {
        String appendQuestionMark = uri.toString().contains("?") ? "&" : "?";
        return Uri.parse(uri.toString() + appendQuestionMark + parameter + "=" + value);
    }

    public static String urlEncodeSpacesOnly(String url) {
        return url.replace(" ", "%20");
    }

    @Nullable
    public static <T> T safeFromJson(@NonNull String json, @NonNull Class<T> classOfT) {
        try {
            return LoganSquare.parse(json, classOfT);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }

    @Nullable
    public static <T> ArrayList<T> safeListFromJson(@NonNull String json, @NonNull Class<T> classOfT) {
        try {
            return (ArrayList<T>) LoganSquare.parseList(json, classOfT);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }


    @Nullable
    public static <T> HashMap<String, T> safeMapFromJson(@NonNull String json, @NonNull Class<T> classOfT) {
        try {
            return (HashMap<String, T>) LoganSquare.parseMap(json, classOfT);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }


    @Nullable
    public static <T> HashMap<String, T> safeMapFromJson(@NonNull InputStream in, @NonNull Class<T> classOfT) {
        try {
            return (HashMap<String, T>) LoganSquare.parseMap(in, classOfT);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }

    @Nullable
    public static String safeToJson(@NonNull Object object) {
        try {
            return LoganSquare.serialize(object);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }


    @Nullable
    public static <T> String safeToJson(@NonNull List<T> objects, Class<T> classOfT) {
        try {
            return LoganSquare.serialize(objects, classOfT);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }

    @Nullable
    public static <T> String safeToJson(@NonNull T[] objects, Class<T> classOfT) {
        try {
            return LoganSquare.serialize(Arrays.asList(objects), classOfT);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
        return null;
    }

    public static void registerLoganSquareTypeConverters() {
        LoganSquare.registerTypeConverter(Calendar.class, new CalendarTypeConverter());
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar valid = null;
        if (date != null) {
            valid = Calendar.getInstance();
            valid.setTime(date);
        }
        return valid;
    }
}
