package com.chrisprime.primestationonecontrol.utilities;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class CalendarTypeConverter extends StringBasedTypeConverter<Calendar> {
    @Override
    public Calendar getFromString(String string) {

        if (string == null) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(string));
        } catch (ParseException e) {
            Timber.e(e, "Failed to parse Calendar. Error: %s", e.getLocalizedMessage());
            return null;
        }
        return calendar;
    }

    @Override
    public String convertToString(Calendar object) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(object.getTime());
    }
}
