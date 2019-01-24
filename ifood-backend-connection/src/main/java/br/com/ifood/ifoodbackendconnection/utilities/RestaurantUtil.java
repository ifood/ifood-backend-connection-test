package br.com.ifood.ifoodbackendconnection.utilities;

import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

public class RestaurantUtil {

    public static boolean isClosed(LocalDateTime time) {
        return time.getHour() < 10 || time.getHour() == 23;
    }

    public static boolean isScheduledUnavailable(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                                                 LocalDateTime time) {

        if (unavailabilityScheduleResponseList == null) return false;

        return unavailabilityScheduleResponseList.stream()
                .anyMatch(unavailabilitySchedule -> unavailabilitySchedule.isUnavailable(time));
    }
}