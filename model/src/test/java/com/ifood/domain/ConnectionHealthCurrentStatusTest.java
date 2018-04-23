package com.ifood.domain;

import com.ifood.helper.TestClock;
import org.junit.Assert;
import org.junit.Test;

public class ConnectionHealthCurrentStatusTest {

    @Test
    public void testIfOnlineWhenInOpenTimeAndConnectionOK(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());
        ConnectionHealthCurrentStatus connectionHealthCurrentStatus = new ConnectionHealthCurrentStatus("restaurant1", true, restaurantAvailability);
        Assert.assertTrue(connectionHealthCurrentStatus.isOnline());
    }

    @Test
    public void testIfOfflineWhenInClosedTimeAndConnectionOK(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayUnavailableAppTime());
        ConnectionHealthCurrentStatus connectionHealthCurrentStatus = new ConnectionHealthCurrentStatus("restaurant1", true, restaurantAvailability);
        Assert.assertFalse(connectionHealthCurrentStatus.isOnline());
    }

    @Test
    public void testIfOfflineWhenInClosedTimeAndConnectionNOK(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayUnavailableAppTime());
        ConnectionHealthCurrentStatus connectionHealthCurrentStatus = new ConnectionHealthCurrentStatus("restaurant1", false, restaurantAvailability);
        Assert.assertFalse(connectionHealthCurrentStatus.isOnline());
    }

    @Test
    public void testIfOfflineWhenInOpenTimeAndConnectionNOK(){
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());
        ConnectionHealthCurrentStatus connectionHealthCurrentStatus = new ConnectionHealthCurrentStatus("restaurant1", false, restaurantAvailability);
        Assert.assertFalse(connectionHealthCurrentStatus.isOnline());
    }

}