package com.ifood;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public LocalDateTime format(String dateString){
        return LocalDateTime.parse(dateString, formatter);
    }

    public String format(LocalDateTime dateTime){
        return dateTime.format(formatter);
    }


}
