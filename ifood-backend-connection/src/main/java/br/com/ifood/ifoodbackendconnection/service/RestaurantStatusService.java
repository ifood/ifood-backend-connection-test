package br.com.ifood.ifoodbackendconnection.service;

import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;
import br.com.ifood.ifoodbackendconnection.utilities.RestaurantUtil;
import br.com.ifood.ifoodbackendconnection.domain.ConnectionInterval;
import br.com.ifood.ifoodbackendconnection.domain.RestaurantStatus;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static br.com.ifood.ifoodbackendconnection.domain.ConnectionInterval.ConnectionDefinition.*;
import static br.com.ifood.ifoodbackendconnection.domain.ConnectionInterval.ConnectionDefinition.APP_CLOSED;
import static br.com.ifood.ifoodbackendconnection.domain.ConnectionInterval.ConnectionDefinition.SUCCEEDED;

@Service
public class RestaurantStatusService {

    private Ignite ignite;
    private RestaurantStatus restaurantStatus;

    @Autowired
    public RestaurantStatusService(Ignite ignite) {
        this.ignite = ignite;
        this.restaurantStatus = new RestaurantStatus();
    }

    public RestaurantStatus fetchRestaurantStatus(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                                                  List<ConnectionInterval> failedConnections,
                                                  List<ConnectionInterval> successfulConnections){
        getSupposedFailed(unavailabilityScheduleResponseList, failedConnections).forEach(this::analyzeFailedConnectionsFor);
        getSupposedSucceeded(unavailabilityScheduleResponseList, successfulConnections).forEach(this::analyzeSuccessfulConnectionsFor);

        return restaurantStatus;
    }

    private Collection<ConnectionInterval> getSupposedSucceeded(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                                                                List<ConnectionInterval> successfulConnections) {
        Collection<ConnectionInterval> supposedSucceeded = new ArrayList<>();
        if(successfulConnections != null && !successfulConnections.isEmpty()) {
            supposedSucceeded = ignite.compute().apply(
                    (ConnectionInterval connectionInterval) ->
                            evalSucceededConnectionPeriod(unavailabilityScheduleResponseList, connectionInterval),
                    successfulConnections
            );
        }
        return supposedSucceeded;
    }

    private Collection<ConnectionInterval> getSupposedFailed(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                                                             List<ConnectionInterval> failedConnections) {
        Collection<ConnectionInterval> supposedFailed = new ArrayList<>();

        if(failedConnections != null && !failedConnections.isEmpty()) {
            supposedFailed = ignite.compute().apply(
                    (ConnectionInterval connectionInterval) ->
                            evalFailedConnectionPeriod(unavailabilityScheduleResponseList, connectionInterval),
                    failedConnections
            );
        }
        return supposedFailed;
    }

    private ConnectionInterval evalSucceededConnectionPeriod(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                                                             ConnectionInterval connectionInterval) {
        LocalDateTime firstReceivedSignal = connectionInterval.getInitialKeepAliveSignal().getReceivedSignal();
        LocalDateTime secondReceivedSignal = connectionInterval.getFinalKeepAliveSignal().getReceivedSignal();

        if (eval(firstReceivedSignal, secondReceivedSignal))
            connectionInterval.setConnectionDefinition(ConnectionInterval.ConnectionDefinition.APP_CLOSED);
        else if (eval(unavailabilityScheduleResponseList, firstReceivedSignal, secondReceivedSignal)){
            connectionInterval.setConnectionDefinition(SCHEDULED_BUSINESS_ISSUES);
        } else {
            connectionInterval.setConnectionDefinition(SUCCEEDED);
        }

        return connectionInterval;
    }

    private ConnectionInterval evalFailedConnectionPeriod(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                                                          ConnectionInterval connectionInterval) {
        LocalDateTime firstReceivedSignal = connectionInterval.getInitialKeepAliveSignal().getReceivedSignal();
        LocalDateTime secondReceivedSignal = connectionInterval.getFinalKeepAliveSignal().getReceivedSignal();

        if (eval(firstReceivedSignal, secondReceivedSignal))
            connectionInterval.setConnectionDefinition(ConnectionInterval.ConnectionDefinition.APP_CLOSED);
        else if (eval(unavailabilityScheduleResponseList, firstReceivedSignal, secondReceivedSignal)){
            connectionInterval.setConnectionDefinition(SCHEDULED_CONNECTION_ISSUES);
        } else {
            connectionInterval.setConnectionDefinition(FAILED);
        }

        return connectionInterval;
    }

    private void analyzeFailedConnectionsFor(ConnectionInterval connectionPeriod) {
        analyze(connectionPeriod, SCHEDULED_CONNECTION_ISSUES, restaurantStatus.getConnectionIssuesScheduled());
        analyze(connectionPeriod, restaurantStatus.getClientAppClosed());
        analyze(connectionPeriod, FAILED, restaurantStatus.getConnectionFailed());
    }

    private void analyzeSuccessfulConnectionsFor(ConnectionInterval connectionPeriod) {
        analyze(connectionPeriod, SCHEDULED_BUSINESS_ISSUES, restaurantStatus.getBusinessIssuesScheduled());
        analyze(connectionPeriod, restaurantStatus.getClientAppClosed());
        analyze(connectionPeriod, SUCCEEDED, restaurantStatus.getAvailableOnline());
    }

    private boolean eval(LocalDateTime firstHealthSignal, LocalDateTime secondHealthSignal) {
        if (RestaurantUtil.isClosed(firstHealthSignal) || RestaurantUtil.isClosed(secondHealthSignal))
            return true;
        return false;
    }

    private boolean eval(List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList,
                         LocalDateTime firstHealthSignal, LocalDateTime secondHealthSignal) {
        if (RestaurantUtil.isScheduledUnavailable(unavailabilityScheduleResponseList, firstHealthSignal)
                || RestaurantUtil.isScheduledUnavailable(unavailabilityScheduleResponseList, secondHealthSignal))
            return true;

        return false;
    }

    private void analyze(ConnectionInterval connectionPeriod,
                         ConnectionInterval.ConnectionDefinition connectionDefinition,
                         List<ConnectionInterval> connectionIntervalList) {
        if (connectionPeriod.getConnectionDefinition().equals(connectionDefinition))
            connectionIntervalList.add(connectionPeriod);
    }

    private void analyze(ConnectionInterval connectionPeriod, List<ConnectionInterval> connectionIntervalList) {
        if(connectionPeriod.getConnectionDefinition().equals(APP_CLOSED))
            connectionIntervalList.add(connectionPeriod);
    }
}