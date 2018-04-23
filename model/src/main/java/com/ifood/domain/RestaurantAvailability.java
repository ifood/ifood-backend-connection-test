package com.ifood.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Responsible for check the availability of the restaturant
 */
public class RestaurantAvailability {

    /**
     * Used for testing issues
     */
    //FIXME: Find a better way to do this instead of declaring this.
    private Clock clock = Clock.systemDefaultZone();

    private List<UnavailabilitySchedule> availabilityUnavailabilitySchedule;

    public RestaurantAvailability() {
    }

    public RestaurantAvailability(List<UnavailabilitySchedule> availabilityUnavailabilitySchedule) {
        this.availabilityUnavailabilitySchedule = availabilityUnavailabilitySchedule;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * If there is not scheduled repository for the current time and is between open time.
     *
     * @return true is there is not a scheduled repository.
     */
    public boolean isCurrentlyOpen(){
        LocalDateTime currentTime = LocalDateTime.now(clock);
        return !isAppUnavailable(currentTime) && !isScheduledUnavailable(currentTime);
    }

    /**
     * The open time availability.
     *
     * @return True if the currentTime is outside the availability window.
     */
    public boolean isAppUnavailable(LocalDateTime time) {
        return time.getHour() < 10 || time.getHour() == 23;
    }

    /**
     * Check if the current time is between repository scheduled.
     *
     * @return True if there is between repository scheduled
     */
    public boolean isScheduledUnavailable(LocalDateTime time) {
        if (availabilityUnavailabilitySchedule == null) return false;

        return availabilityUnavailabilitySchedule.stream() //
                .anyMatch(unavailabilitySchedule -> unavailabilitySchedule.isUnavailable(time));
    }
}
