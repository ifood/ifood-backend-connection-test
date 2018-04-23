package com.ifood.domain;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class UnavailabilityScheduleTest {

    @Test
    public void testSchedule(){
        LocalDateTime initialSchedule = LocalDateTime.now();
        LocalDateTime finalSchedule = LocalDateTime.now().plusMinutes(1);
        UnavailabilitySchedule unavailabilitySchedule = new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, initialSchedule, finalSchedule);
        assertNotNull(unavailabilitySchedule);
    }

    @Test
    public void testIfDateIsBetweenUnavailable(){
        LocalDateTime initialSchedule = LocalDateTime.now();
        LocalDateTime finalSchedule = LocalDateTime.now().plusMinutes(1);
        UnavailabilitySchedule unavailabilitySchedule = new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, initialSchedule, finalSchedule);
        assertTrue(unavailabilitySchedule.isUnavailable(LocalDateTime.now()));
    }

    @Test
    public void testIfDateIsNotBetweenUnavailable(){
        LocalDateTime initialSchedule = LocalDateTime.now().minusHours(2);
        LocalDateTime finalSchedule = LocalDateTime.now().minusMinutes(1);
        UnavailabilitySchedule unavailabilitySchedule = new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, initialSchedule, finalSchedule);
        assertFalse(unavailabilitySchedule.isUnavailable(LocalDateTime.now()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIfInitialScheduleIsGreaterThanFinalSchedule(){
        new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, LocalDateTime.now(), LocalDateTime.now().minusMinutes(1));
    }

}