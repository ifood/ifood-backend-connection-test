package com.ifood.domain;

import com.ifood.domain.*;
import com.ifood.helper.TestClock;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConnectionReportTest {

    @Test
    public void testReportGenerationForPeriodsWithoutIncidents(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(TestClock.getTodayAvailableAppTime());

        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(1)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(3)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(4)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(5)));

        RestaurantConnection restaurantConnection =new RestaurantConnection(connectionHealthSignals);
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());

        ConnectionReport connectionReport = new ConnectionReport(restaurantAvailability, restaurantConnection);
        Assert.assertEquals(4, connectionReport.getConnectionSucceceded().size());
        Assert.assertEquals(0, connectionReport.getBussinessIssuesScheduled().size());
        Assert.assertEquals(0, connectionReport.getConnectionIssuesScheduled().size());
        Assert.assertEquals(0, connectionReport.getConnectionFailed().size());
    }

    @Test
    public void testReportGenerationForPeriodsWithSchedulesWithoutIncidents(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(TestClock.getTodayAvailableAppTime());

        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(1)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(3)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(4)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(5)));

        List<UnavailabilitySchedule> unavailabilitySchedules = new ArrayList<>();
        unavailabilitySchedules.add(new UnavailabilitySchedule(UnavailabilityReason.OVERLOADED, now.plusMinutes(1), now.plusMinutes(4)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability(unavailabilitySchedules);
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());

        ConnectionReport connectionReport = new ConnectionReport(restaurantAvailability, restaurantConnection);
        Assert.assertEquals(1, connectionReport.getConnectionSucceceded().size());
        Assert.assertEquals(3, connectionReport.getBussinessIssuesScheduled().size());
        Assert.assertEquals(0, connectionReport.getConnectionIssuesScheduled().size());
        Assert.assertEquals(0, connectionReport.getConnectionFailed().size());
    }

    @Test
    public void testReportGenerationForPeriodsWithSchedulesWithIncidents(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(TestClock.getTodayAvailableAppTime());

        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(3)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(6)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(9)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(12)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(15)));

        List<UnavailabilitySchedule> unavailabilitySchedules = new ArrayList<>();
        unavailabilitySchedules.add(new UnavailabilitySchedule(UnavailabilityReason.CONNECTION_ISSUES, now.plusMinutes(3), now.plusMinutes(12)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability(unavailabilitySchedules);
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());

        ConnectionReport connectionReport = new ConnectionReport(restaurantAvailability, restaurantConnection);
        Assert.assertEquals(0, connectionReport.getConnectionSucceceded().size());
        Assert.assertEquals(0, connectionReport.getBussinessIssuesScheduled().size());
        Assert.assertEquals(3, connectionReport.getConnectionIssuesScheduled().size());
        Assert.assertEquals(1, connectionReport.getConnectionFailed().size());
    }


    @Test
    public void testReportGenerationForPeriodsWithoutSchedulesWithIncidents(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(TestClock.getTodayAvailableAppTime());

        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(1)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(3)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(6)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(9)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(12)));

        RestaurantConnection restaurantConnection =new RestaurantConnection(connectionHealthSignals);
        RestaurantAvailability restaurantAvailability = new RestaurantAvailability();
        restaurantAvailability.setClock(TestClock.getTodayAvailableAppTime());


        ConnectionReport connectionReport = new ConnectionReport(restaurantAvailability, restaurantConnection);
        Assert.assertEquals(0, connectionReport.getConnectionSucceceded().size());
        Assert.assertEquals(0, connectionReport.getBussinessIssuesScheduled().size());
        Assert.assertEquals(0, connectionReport.getConnectionIssuesScheduled().size());
        Assert.assertEquals(4, connectionReport.getConnectionFailed().size());

    }

}
