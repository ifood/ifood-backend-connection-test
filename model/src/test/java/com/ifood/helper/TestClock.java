package com.ifood.helper;

import java.time.*;

public class TestClock {

    private static final ZoneOffset BRAZIL_ZONEOFFSET = ZoneOffset.of("-3");

    public static Clock getTodayAvailableAppTime(){
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        int day = LocalDateTime.now().getDayOfMonth();
        return Clock.fixed(LocalDateTime.of(year, month, day, 10,1).toInstant(BRAZIL_ZONEOFFSET), ZoneId.of("Brazil/East"));
    }


    public static Clock getTodayUnavailableAppTime(){
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        int day = LocalDateTime.now().getDayOfMonth();
        return Clock.fixed(LocalDateTime.of(year, month, day, 9,1).toInstant(BRAZIL_ZONEOFFSET), ZoneId.of("Brazil/East"));
    }

}
