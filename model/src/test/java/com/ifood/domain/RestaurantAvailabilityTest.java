package com.ifood.domain;

import com.ifood.domain.RestaurantAvailability;
import com.ifood.domain.UnavailabilityReason;
import com.ifood.domain.UnavailabilitySchedule;
import com.ifood.helper.TestClock;
import org.junit.Test;

import java.time.*;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RestaurantAvailabilityTest {

    @Test
    public void testIfCurrentlyOpenWithoutScheduledUnavailability(){
        LocalDateTime now = LocalDateTime.now(TestClock.getTodayAvailableAppTime());
        List<UnavailabilitySchedule> unavailabilitySchedule = List.of(
                new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, now.minusHours(2), now.minusHours(1)), //
                new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, now.plusHours(1), now.plusHours(2))
        );
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability(unavailabilitySchedule);
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());
        assertTrue(restaurantAvailability.isCurrentlyOpen());
    }

    @Test
    public void testCurrentlyClosedWithScheduledUnavailability(){
        LocalDateTime now = LocalDateTime.now(TestClock.getTodayAvailableAppTime());

        List<UnavailabilitySchedule> unavailabilitySchedule = List.of(
                new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, now.minusHours(1), now.plusHours(1)), //
                new UnavailabilitySchedule(UnavailabilityReason.HOLIDAYS, now.plusHours(4), now.plusHours(5))
        );
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability(unavailabilitySchedule);
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());
        assertFalse(restaurantAvailability.isCurrentlyOpen());
    }

    @Test
    public void testCurrentlyClosedDueToAppClosed(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayUnavailableAppTime());
        assertFalse(restaurantAvailability.isCurrentlyOpen());
    }

    @Test
    public void testIfCurrentlyOpenIfAppIsOpen(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());
        assertTrue(restaurantAvailability.isCurrentlyOpen());
    }

    @Test
    public void testIfOpenAtWorkTime(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        assertFalse(restaurantAvailability.isAppUnavailable(LocalDateTime.now(TestClock.getTodayAvailableAppTime())));
    }

    @Test
    public void testIfClosedOutWorkTime(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        assertTrue(restaurantAvailability.isAppUnavailable(
                LocalDateTime.now(TestClock.getTodayUnavailableAppTime())
        ));
    }

}