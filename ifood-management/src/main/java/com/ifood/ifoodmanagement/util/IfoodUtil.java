package com.ifood.ifoodmanagement.util;

import com.ifood.ifoodmanagement.domain.ConnectionState;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.*;

public class IfoodUtil {

    private static final String OPENING_HOUR_START = "10";
    private static final String OPENING_HOUR_END = "23";
    private static final int KEEP_ALIVE_THRESHOLD_MINUTES = 2;

    private static final List<ConnectionState> CONNECTION_STATES_VALUES =
            Collections.unmodifiableList(Arrays.asList(ConnectionState.values()));
    private static final int SIZE = CONNECTION_STATES_VALUES.size();
    private static final Random RANDOM = new Random();


    private IfoodUtil() { throw new IllegalStateException("Utility class"); }

    public static boolean isRestaurantOnline(boolean isAvailable, DateTime lastModified){

        return (isClientInsideOpeningHour() &&
                isKeepAliveUnderThreshold(lastModified) &&
                isAvailable) ? true : false;
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

    public static ConnectionState getRandomConnectionState(){
        return CONNECTION_STATES_VALUES.get(RANDOM.nextInt(SIZE));
    }
}
