package com.ifood.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for checking the connection health.
 */
public class RestaurantConnection {

    /**
     * Acceptable Signal Delay in minutes.
     */
    private static final long ACCEPTABLE_SIGNAL_DELAY_IN_MINUTES = 2;

    private final List<ConnectionHealthSignal> connectionHealthSignals;

    private final List<ConnectionPeriodAssessed> connectionFailed;

    private final List<ConnectionPeriodAssessed> connectionSucceded;

    public RestaurantConnection(List<ConnectionHealthSignal> connectionHealthSignals) {
        this.connectionHealthSignals = connectionHealthSignals;
        this.connectionFailed = new ArrayList<>();
        this.connectionSucceded = new ArrayList<>();
        searchForConnectionsFailures();
    }

    /**
     * Search by connections problems that happened. It is considered the ACCEPTABLE_SIGNAL_DELAY_IN_MINUTES
     * @throws IllegalStateException in case the connection could not be tested, eg: there is no tries registered or less than two.
     */
    private void searchForConnectionsFailures(){
        if (connectionHealthSignals == null || connectionHealthSignals.size() < 2) {
            throw new IllegalStateException("It was not possible to check if the connection is OK.");
        }

        Collections.sort(connectionHealthSignals);

        ConnectionHealthSignal currentHealthSignal = null;
        for (ConnectionHealthSignal nextConnectionHealthSignal : connectionHealthSignals){
            if(currentHealthSignal == null){
                currentHealthSignal = nextConnectionHealthSignal;
            } else {
                Duration differenceBetweenSignal = currentHealthSignal.getDifferenceBetweenSignal(nextConnectionHealthSignal);
                if (differenceBetweenSignal.toMinutes() >= ACCEPTABLE_SIGNAL_DELAY_IN_MINUTES){
                    connectionFailed.add(new ConnectionPeriodAssessed(currentHealthSignal, nextConnectionHealthSignal));

                } else {
                    connectionSucceded.add(new ConnectionPeriodAssessed(currentHealthSignal, nextConnectionHealthSignal));
                }
                currentHealthSignal = nextConnectionHealthSignal;
            }
        }
    }


    public List<ConnectionPeriodAssessed> getConnectionsFailed(){
        return connectionFailed;
    }


    public List<ConnectionPeriodAssessed> getConnectionsSucceded(){
        return connectionSucceded;
    }
}
