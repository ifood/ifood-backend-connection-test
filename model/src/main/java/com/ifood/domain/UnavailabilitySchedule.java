package com.ifood.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ifood.database.entity.UnavailabilityScheduleEntity;

import java.time.LocalDateTime;

/**
 * Scheduled date for an repository
 */
public class UnavailabilitySchedule {

    private String code;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime scheduleStart;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime scheduleEnd;

    private UnavailabilityReason unavailabilityReason;

    public UnavailabilitySchedule() {
    }

    public UnavailabilitySchedule(UnavailabilityReason unavailabilityReason, LocalDateTime scheduleStart, LocalDateTime scheduleEnd) {
        if (scheduleStart.isAfter(scheduleEnd)){
            throw new IllegalArgumentException("Initial repository is greater than the final repository.");
        }

        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
        this.unavailabilityReason = unavailabilityReason;
    }

    public UnavailabilitySchedule(UnavailabilityScheduleEntity unavailabilityScheduleEntity) {
        this.code = unavailabilityScheduleEntity.getCode();
        this.unavailabilityReason = UnavailabilityReason.valueOf(unavailabilityScheduleEntity.getReason());
        this.scheduleStart = unavailabilityScheduleEntity.getScheduleStart();
        this.scheduleEnd = unavailabilityScheduleEntity.getScheduleEnd();
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getScheduleStart() {
        return scheduleStart;
    }

    public LocalDateTime getScheduleEnd() {
        return scheduleEnd;
    }

    public UnavailabilityReason getUnavailabilityReason() {
        return unavailabilityReason;
    }

    /**
     * Check if the time time provided is between the scheduled repository.
     * @param localDateTime - The time the will be checked
     * @return true if the given time is between the repository time.
     */
    public boolean isUnavailable(LocalDateTime localDateTime){
        return localDateTime.isAfter(scheduleStart) && localDateTime.isBefore(scheduleEnd);
    }
}
