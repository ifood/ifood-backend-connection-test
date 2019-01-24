package br.com.ifood.ifoodbackendconnection.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static LocalDateTime format(String dateString){
        return LocalDateTime.parse(dateString, formatter);
    }

    public static String format(LocalDateTime dateTime){
        return dateTime.format(formatter);
    }
}