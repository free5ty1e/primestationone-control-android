package com.chrisprime.primestationonecontrol.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.TimeUtils;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A semi-sophisticated time formatter, that formats time in human relative terms.
 */
public class TimeFormatter {

    final Resources mResources;

    public TimeFormatter(Resources resources) {
        mResources = resources;
    }

    public static String getFriendlyTime(long periodInMilliseconds, boolean inPast) {
        Context appResourcesContext = PrimeStationOneControlApplication.instance;
        StringBuilder stringBuilder = new StringBuilder();
        long remainingTimeDifference = periodInMilliseconds / 1000;

        long seconds = (remainingTimeDifference >= 60 ? remainingTimeDifference % 60 : remainingTimeDifference);
        long minutes = (remainingTimeDifference = (long) Math.ceil((double) remainingTimeDifference / 60)) >= 60 ? remainingTimeDifference % 60 : remainingTimeDifference;
        long hours = (remainingTimeDifference = (remainingTimeDifference / 60)) >= 24 ? remainingTimeDifference % 24 : remainingTimeDifference;
        long days = (remainingTimeDifference = (remainingTimeDifference / 24)) >= 30 ? remainingTimeDifference % 30 : remainingTimeDifference;
        long months = (remainingTimeDifference = (remainingTimeDifference / 30)) >= 12 ? remainingTimeDifference % 12 : remainingTimeDifference;
        long years = (remainingTimeDifference / 12);

        if (years > 0) {
            stringBuilder.append(appResourcesContext.getResources().getQuantityString(R.plurals.plural_years, (int) years, (int) years));
        } else if (months > 0) {
            stringBuilder.append(appResourcesContext.getResources().getQuantityString(R.plurals.plural_months, (int) months, (int) months));
        } else if (days > 0) {
            stringBuilder.append(appResourcesContext.getResources().getQuantityString(R.plurals.plural_days, (int) days, (int) days));
        } else if (hours > 0) {
            stringBuilder.append(appResourcesContext.getResources().getQuantityString(R.plurals.plural_hours, (int) hours, (int) hours));
        } else if (minutes > 0) {
            stringBuilder.append(appResourcesContext.getResources().getQuantityString(R.plurals.plural_minutes, (int) minutes, (int) minutes));
        } else {
            stringBuilder.append(appResourcesContext.getResources().getQuantityString(R.plurals.plural_seconds, (int) seconds, (int) seconds));
        }

        if (inPast) {
            stringBuilder.append(appResourcesContext.getString(R.string.friendly_time_ago));
        }
        return stringBuilder.toString();
    }

    @Nullable
    public static String getPrettyPrintTime(@Nullable Calendar calendar) {
        return format("yyyy-MM-dd HH:mm:ss", calendar, false);
    }

    /**
     * Formats the specified time.  The placeholder %1$s in the specified resource will be replaced
     * by the month and day (for future and past days) or the time (for today).
     * <p>
     * If the time is some date in the future then it will use the
     * format specified by resourceIdForFutureDay (example "Launches on Nov 27").
     * <p>
     * If the time is today and the specified time is in the future it will use the
     * resourceIdForTodayInFuture (example "Launches at 5:00pm).
     * <p>
     * If the time is today and the specified time is in the past it will use the
     * resourceIfForTodayInPast (example "Launched at 5:00pm" or "CLOSED")
     * <p>
     * If the time is some date in the past then it will use the format specified
     * by resourceIdForPastDay (example "Launched on Nov 27")
     * <p>
     * If now is equal to the milliseconds to the specified time then it will use the
     * resourceIdForTodayInFuture
     * <p>
     * Please note this uses the TimeManagers time to determine relative time.
     *
     * @param resourceIdForFutureDay     resource to use if date in future
     * @param resourceIdForTodayInFuture resource to use if time in future and it is today
     * @param resourceIdForTodayInPast   resource to use if time is in the past and it is today
     * @param resourceIdForPastDay       resource to use if the date is in the past
     * @param time                       the time to format
     */

    public String formatAsHumanReadable(@StringRes int resourceIdForFutureDay,
                                        @StringRes int resourceIdForTodayInFuture,
                                        @StringRes int resourceIdForTodayInPast,
                                        @StringRes int resourceIdForPastDay, long time) {
        String results;

        long nowInMs = TimeManager.Companion.getInstance().currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(nowInMs);
        now.setTimeZone(TimeZone.getDefault());  //use the user's time zone, not UTC to figure out what is "now"

        Calendar importantDateAndTime = Calendar.getInstance();
        importantDateAndTime.setTimeInMillis(time);
        importantDateAndTime.setTimeZone(TimeZone.getDefault());


        if (now.get(Calendar.YEAR) == importantDateAndTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == importantDateAndTime.get(Calendar.DAY_OF_YEAR)) {
            //it is today
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.US);
            if (time >= nowInMs) {
                // future & today
                results = String.format(mResources.getString(resourceIdForTodayInFuture), formatter.format(time));
            } else {
                // today in the past
                results = String.format(mResources.getString(resourceIdForTodayInPast), formatter.format(time));
            }
        } else if (time >= nowInMs) {
            //future & not today
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd", Locale.US);
            results = String.format(mResources.getString(resourceIdForFutureDay), formatter.format(time));
        } else { //(time < nowInMs)
            // that is the past & not today
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd", Locale.US);
            results = String.format(mResources.getString(resourceIdForPastDay), formatter.format(time));
        }

        return results;
    }

    @NonNull
    public static String getTwelveHourTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma", Locale.US);
        return dateFormat.format(new Date(timeInMillis));
    }

    @NonNull
    public static String getDurationString(long durationInMillis) {
        // Just for debugging; not internationalized.
        StringBuilder sb = new StringBuilder();
        TimeUtils.formatDuration(durationInMillis, sb);
        return sb.toString();
    }

    @Nullable
    public static String format(@StringRes int templateRes, @Nullable Calendar calendar, boolean lowerCaseAmPm) {
        return format(PrimeStationOneControlApplication.instance.getString(templateRes), calendar, lowerCaseAmPm);
    }

    @Nullable
    public static String format(@NonNull String template, @Nullable Calendar calendar, boolean lowerCaseAmPm) {
        if (calendar == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.US);
        if (lowerCaseAmPm) {
            // force lowercase AM/PM (no way to do it via format)
            DateFormatSymbols symbols = formatter.getDateFormatSymbols();
            symbols.setAmPmStrings(new String[]{"am", "pm"});
            formatter.setDateFormatSymbols(symbols);
        }
        return formatter.format(calendar.getTime());
    }
}
