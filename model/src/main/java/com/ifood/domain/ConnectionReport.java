package com.ifood.domain;

import org.apache.ignite.Ignite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.ifood.domain.ConnectionPeriodAssessed.ConnectionDefinition.*;

/**
 * Prepare the connection reports showing the scheduled and unscheduled issues.
 *
 * //FIXME This class is messy.
 */
public class ConnectionReport {

    private final List<ConnectionPeriodAssessed> connectionSucceceded;
    private final List<ConnectionPeriodAssessed> connectionFailed;
    private final List<ConnectionPeriodAssessed> connectionIssuesScheduled;
    private final List<ConnectionPeriodAssessed> bussinessIssuesScheduled;
    private final List<ConnectionPeriodAssessed> appClosed;

    public ConnectionReport() {
        this.connectionSucceceded = new ArrayList<>();
        this.connectionFailed = new ArrayList<>();
        this.connectionIssuesScheduled = new ArrayList<>();
        this.bussinessIssuesScheduled = new ArrayList<>();
        this.appClosed = new ArrayList<>();
    }

    public ConnectionReport(final RestaurantAvailability restaurantAvailability, final RestaurantConnection restaurantConnection){
        this();
        Collection<ConnectionPeriodAssessed> supposedFailed = new ArrayList<>();
        if(restaurantConnection.getConnectionsFailed() != null && !restaurantConnection.getConnectionsFailed().isEmpty()) {
            supposedFailed = restaurantConnection.getConnectionsFailed().stream()
                    .map(connectionPeriodAssessed -> assessFailedConnectionPeriod(restaurantAvailability, connectionPeriodAssessed)).collect(Collectors.toList());
        }

        Collection<ConnectionPeriodAssessed> supposedSucceded = new ArrayList<>();
        if(restaurantConnection.getConnectionsSucceded() != null && !restaurantConnection.getConnectionsSucceded().isEmpty()) {
            supposedSucceded = restaurantConnection.getConnectionsSucceded().stream()
                    .map(connectionPeriodAssessed -> assessSuccededConnectionPeriod(restaurantAvailability, connectionPeriodAssessed)).collect(Collectors.toList());
        }

        supposedFailed.forEach(this::defineFailedConnectionList);
        supposedSucceded.forEach(this::defineSuccededConnectionList);
    }


    public ConnectionReport(final RestaurantAvailability restaurantAvailability, final RestaurantConnection restaurantConnection, Ignite ignite){
        this();
        Collection<ConnectionPeriodAssessed> supposedFailed = new ArrayList<>();
        if(restaurantConnection.getConnectionsFailed() != null && !restaurantConnection.getConnectionsFailed().isEmpty()) {
            supposedFailed = ignite.compute().apply(
                    (ConnectionPeriodAssessed connectionPeriodAssessed) ->
                            assessFailedConnectionPeriod(restaurantAvailability, connectionPeriodAssessed),
                            restaurantConnection.getConnectionsFailed()
            );
        }

        Collection<ConnectionPeriodAssessed> supposedSucceded = new ArrayList<>();
        if(restaurantConnection.getConnectionsSucceded() != null && !restaurantConnection.getConnectionsSucceded().isEmpty()) {
            supposedSucceded = ignite.compute().apply(
                    (ConnectionPeriodAssessed connectionPeriodAssessed) ->
                            assessSuccededConnectionPeriod(restaurantAvailability, connectionPeriodAssessed),
                            restaurantConnection.getConnectionsSucceded()
            );
        }

        supposedFailed.forEach(this::defineFailedConnectionList);
        supposedSucceded.forEach(this::defineSuccededConnectionList);
    }

    private ConnectionPeriodAssessed assessSuccededConnectionPeriod(RestaurantAvailability restaurantAvailability, ConnectionPeriodAssessed connectionPeriodAssessed) {
        LocalDateTime firstHealthSignal = connectionPeriodAssessed.getFirstHealthSignal();
        LocalDateTime scondHealthSignal = connectionPeriodAssessed.getSecondHealthSignal();
        if (restaurantAvailability.isAppUnavailable(firstHealthSignal) || restaurantAvailability.isAppUnavailable(scondHealthSignal)) {
            connectionPeriodAssessed.setConnectionDefinition(ConnectionPeriodAssessed.ConnectionDefinition.APP_CLOSED);
        } else if (restaurantAvailability.isScheduledUnavailable(firstHealthSignal) || restaurantAvailability.isScheduledUnavailable(scondHealthSignal)) {
            connectionPeriodAssessed.setConnectionDefinition(SCHEDULED_BUSSINESS_ISSUES);
        } else {
            connectionPeriodAssessed.setConnectionDefinition(SUCCEDED);
        }
        return connectionPeriodAssessed;
    }

    private ConnectionPeriodAssessed assessFailedConnectionPeriod(RestaurantAvailability restaurantAvailability, ConnectionPeriodAssessed connectionPeriodAssessed) {
        LocalDateTime firstHealthSignal = connectionPeriodAssessed.getFirstHealthSignal();
        LocalDateTime scondHealthSignal = connectionPeriodAssessed.getSecondHealthSignal();
        if (restaurantAvailability.isAppUnavailable(firstHealthSignal) || restaurantAvailability.isAppUnavailable(scondHealthSignal)) {
            connectionPeriodAssessed.setConnectionDefinition(ConnectionPeriodAssessed.ConnectionDefinition.APP_CLOSED);
        } else  if (restaurantAvailability.isScheduledUnavailable(firstHealthSignal) || restaurantAvailability.isScheduledUnavailable(scondHealthSignal)) {
            connectionPeriodAssessed.setConnectionDefinition(SCHEDULED_CONNECTION_ISSUES);
        } else {
            connectionPeriodAssessed.setConnectionDefinition(FAILED);
        }
        return connectionPeriodAssessed;
    }

    private void defineFailedConnectionList(ConnectionPeriodAssessed connectionPeriodAssessed) {
        if (connectionPeriodAssessed.getConnectionDefinition().equals(SCHEDULED_CONNECTION_ISSUES)){
            connectionIssuesScheduled.add(connectionPeriodAssessed);
        } else if(connectionPeriodAssessed.getConnectionDefinition().equals(APP_CLOSED)) {
            appClosed.add(connectionPeriodAssessed);
        } else if (connectionPeriodAssessed.getConnectionDefinition().equals(FAILED)){
            connectionFailed.add(connectionPeriodAssessed);
        }
    }

    private void defineSuccededConnectionList(ConnectionPeriodAssessed connectionPeriodAssessed) {
        if (connectionPeriodAssessed.getConnectionDefinition().equals(SCHEDULED_BUSSINESS_ISSUES)){
            bussinessIssuesScheduled.add(connectionPeriodAssessed);
        } else if(connectionPeriodAssessed.getConnectionDefinition().equals(APP_CLOSED)) {
            appClosed.add(connectionPeriodAssessed);
        } else if (connectionPeriodAssessed.getConnectionDefinition().equals(SUCCEDED)){
            connectionSucceceded.add(connectionPeriodAssessed);
        }
    }


    public List<ConnectionPeriodAssessed> getConnectionSucceceded() {
        return connectionSucceceded;
    }

    public List<ConnectionPeriodAssessed> getConnectionFailed() {
        return connectionFailed;
    }

    public List<ConnectionPeriodAssessed> getConnectionIssuesScheduled() {
        return connectionIssuesScheduled;
    }

    public List<ConnectionPeriodAssessed> getBussinessIssuesScheduled() {
        return bussinessIssuesScheduled;
    }

    public List<ConnectionPeriodAssessed> getAppClosed() {
        return appClosed;
    }
}
