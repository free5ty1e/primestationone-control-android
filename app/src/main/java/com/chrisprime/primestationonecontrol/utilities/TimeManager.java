package com.chrisprime.primestationonecontrol.utilities;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Handle getting the time.  It will look at the local time and
 * determine if it should be trusted based on the last request
 * to the feed service.
 */
public class TimeManager {

    private static final TimeManager INSTANCE = new TimeManager();

    public static final long ONE_WEEK_IN_MILLIS = 604800000L;
    public static final long ONE_DAY_IN_MILLIS = 86400000L;
    public static final long FIFTEEN_MINS_IN_MILLIS = 900000L;
    public static final long ONE_HOUR_IN_MILLIS = 3600000L;
    public static final long ONE_MINUTE_IN_MILLIS = 60000L;
    public static final long ONE_SECOND_IN_MILLIS = 1000L;

    public static TimeManager getInstance() {
        return INSTANCE;
    }


    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static Calendar currentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis());
        return calendar;
    }

    public static int[] getMinuteSecondDifference(Calendar earlier, Calendar later) {
        int[] differences = new int[3];
        long diff = later.getTimeInMillis() - earlier.getTimeInMillis();
        differences[0] = (int) TimeUnit.MILLISECONDS.toHours(diff);
        differences[1] = (int) TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
        differences[2] = (int) (TimeUnit.MILLISECONDS.toSeconds(diff) % 60 + 1);

        return differences;
    }

    public static boolean timeFrameIsWithinDate(long milliToAdd, int daystoAdd) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, daystoAdd);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        long startOfDay = c.getTimeInMillis();
        long timeFrame = currentTimeMillis() + milliToAdd;
        long endOfDay = startOfDay + ONE_DAY_IN_MILLIS;
        return (timeFrame > startOfDay && timeFrame < endOfDay);
    }

}
