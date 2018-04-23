package com.ifood.domain;

import com.ifood.domain.ConnectionHealthSignal;
import com.ifood.domain.ConnectionPeriodAssessed;
import com.ifood.domain.RestaurantConnection;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RestaurantConnectionTest {

    @Test
    public void testConnectionWithoutFailure(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        connectionHealthSignals.add(new ConnectionHealthSignal(now.minusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.minusMinutes(1)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        List<ConnectionPeriodAssessed> connectionPeriodAssessed = restaurantConnection.getConnectionsFailed();

        assertEquals(0, connectionPeriodAssessed.size());
    }

    @Test
    public void testConnectionWithFailure(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(5)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(10)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(15)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(20)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(25)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        List<ConnectionPeriodAssessed> connectionPeriodAssessed = restaurantConnection.getConnectionsFailed();

        assertEquals(4, connectionPeriodAssessed.size());
    }

    @Test
    public void testConnectionWithManyFailures(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        connectionHealthSignals.add(new ConnectionHealthSignal(now.minusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusSeconds(1)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        List<ConnectionPeriodAssessed> connectionPeriodAssessed = restaurantConnection.getConnectionsFailed();

        assertEquals(1, connectionPeriodAssessed.size());
    }


    @Test
    public void testConnectionWithFailureInALongPeriod(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        connectionHealthSignals.add(new ConnectionHealthSignal(now.minusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusHours(2)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        List<ConnectionPeriodAssessed> connectionPeriodAssessed = restaurantConnection.getConnectionsFailed();

        assertEquals(1, connectionPeriodAssessed.size());
    }

    @Test
    public void testConnectionWithOneFailure(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        connectionHealthSignals.add(new ConnectionHealthSignal(now.minusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusSeconds(1)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusSeconds(10)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusSeconds(30)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(1)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
        List<ConnectionPeriodAssessed> connectionPeriodAssessed = restaurantConnection.getConnectionsFailed();

        assertEquals(1, connectionPeriodAssessed.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testConnectionOnlyOneConnection(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();
        connectionHealthSignals.add(new ConnectionHealthSignal(LocalDateTime.now().minusMinutes(2)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);
    }

    @Test
    public void testConnectionWithFailuresIntervals(){
        List<ConnectionHealthSignal> connectionHealthSignals = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(1)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(2)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(15)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(16)));
        connectionHealthSignals.add(new ConnectionHealthSignal(now.plusMinutes(17)));

        RestaurantConnection restaurantConnection = new RestaurantConnection(connectionHealthSignals);

        assertEquals(1, restaurantConnection.getConnectionsFailed().size());
        assertEquals(13, restaurantConnection.getConnectionsFailed().get(0).getTimeBetween().toMinutes());

        assertEquals(3, restaurantConnection.getConnectionsSucceded().size());
        assertEquals(1, restaurantConnection.getConnectionsSucceded().get(0).getTimeBetween().toMinutes());
        assertEquals(1, restaurantConnection.getConnectionsSucceded().get(1).getTimeBetween().toMinutes());
        assertEquals(1, restaurantConnection.getConnectionsSucceded().get(2).getTimeBetween().toMinutes());

    }

}