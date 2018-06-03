package com.ifood.ifoodmanagement.util;

import com.ifood.ifoodmanagement.domain.Restaurant;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Calendar;

public class IfoodUtil {

    private static final String OPENING_HOUR_START = "10";
    private static final String OPENING_HOUR_END = "23";
    private static final int KEEP_ALIVE_THRESHOLD_MINUTES = 2;

    private IfoodUtil() { throw new IllegalStateException("Utility class"); }

    public static boolean isRestaurantOnline(Restaurant restaurant){

        return (isClientInsideOpeningHour() &&
                isKeepAliveUnderThreshold(restaurant.getLastModified()) &&
                restaurant.isAvailable()) ? true : false;
    }

    private static boolean isClientInsideOpeningHour(){
        String currentHour = Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        return ((currentHour.compareTo(OPENING_HOUR_START) >= 0)
                && (currentHour.compareTo(OPENING_HOUR_END) <= 0));
    }

    private static boolean isKeepAliveUnderThreshold(DateTime lastModified){

        final int minutesSinceLastKeepAlive =
                Minutes.minutesBetween(lastModified, DateTime.now()).getMinutes();

        return minutesSinceLastKeepAlive <= KEEP_ALIVE_THRESHOLD_MINUTES ? true : false;
    }
}
