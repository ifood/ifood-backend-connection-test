package com.ifood.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class UnavailabilityScheduleEntity {
    private RestaurantEntity restaurant;
    private String reason;
    private LocalDateTime scheduleStart;
    private LocalDateTime scheduleEnd;

    public UnavailabilityScheduleEntity(RestaurantEntity restaurant, String reason, LocalDateTime scheduleStart, LocalDateTime scheduleEnd) {
        this.restaurant = restaurant;
        this.reason = reason;
        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getScheduleStart() {
        return scheduleStart;
    }

    public LocalDateTime getScheduleEnd() {
        return scheduleEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnavailabilityScheduleEntity that = (UnavailabilityScheduleEntity) o;
        return Objects.equals(reason, that.reason) &&
                Objects.equals(scheduleStart, that.scheduleStart) &&
                Objects.equals(scheduleEnd, that.scheduleEnd);
    }

    @Override
    public int hashCode() {

        return Objects.hash(reason, scheduleStart, scheduleEnd);
    }
}
