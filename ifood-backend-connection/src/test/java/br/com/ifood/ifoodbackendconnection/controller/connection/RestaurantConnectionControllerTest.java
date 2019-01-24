package br.com.ifood.ifoodbackendconnection.controller.connection;

import br.com.ifood.ifoodbackendconnection.controller.connection.response.RestaurantKeepSignalHistoryResponse;
import br.com.ifood.ifoodbackendconnection.domain.*;
import br.com.ifood.ifoodbackendconnection.service.HealthCheckService;
import br.com.ifood.ifoodbackendconnection.utilities.DateFormatter;
import br.com.ifood.ifoodbackendconnection.utilities.Either;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantConnectionControllerTest {

    @InjectMocks
    private RestaurantConnectionController restaurantConnectionController;

    @Mock
    private HealthCheckService healthCheckService;

    @Test
    public void shouldReceiveNewKeepSignal() {
        String restaurantCode = "aaa472a3-0544-4e68-a3eb-3740d42ece7d";

        Either<ErrorList, SignalHistory> signalHistoryCreated = Either.right(new SignalHistory(new Restaurant(restaurantCode), LocalDateTime.now()));

        when(healthCheckService.saveSignalForRestaurant(restaurantCode)).thenReturn(signalHistoryCreated);

        ResponseEntity keepAliveSignalReceived = restaurantConnectionController.createKeepSignal(restaurantCode);

        assertThat(keepAliveSignalReceived.getStatusCode(), is(HttpStatus.CREATED));

    }

    @Test
    public void shouldFetchCurrentConnectionStatus() {
        List<String> restaurantCodes = Arrays.asList("aaa472a3-0544-4e68-a3eb-3740d42ece7d", "aaa4854a6-0544-4e68-a3eb-3740d42ece7d");

        List<ConnectionStatus> connectionStatusList = Arrays.asList(
                new ConnectionStatusBuilder().withRestaurantCode(restaurantCodes.get(0)).build(),
                new ConnectionStatusBuilder().withRestaurantCode(restaurantCodes.get(1)).build()
        );

        when(healthCheckService.fetchRestaurantConnectionsFor(restaurantCodes)).thenReturn(connectionStatusList);

        ResponseEntity<List<ConnectionStatus>> connectionStatusReturnedList = restaurantConnectionController.fetchCurrentConnectionStatus(restaurantCodes);
        List<String> returnedRestaurantCodes = new ArrayList<>();
        for(ConnectionStatus connectionStatus : connectionStatusReturnedList.getBody()) {
            returnedRestaurantCodes.add(connectionStatus.getRestaurantCode());
        }

        assertThat(connectionStatusReturnedList.getStatusCode(), Matchers.is(HttpStatus.OK));
        assertThat(returnedRestaurantCodes, Matchers.containsInAnyOrder(restaurantCodes.toArray()));
    }

    @Test
    public void shouldFetchRestaurantHealthHistoryGivenRestaurantListAndPeriod() {
        List<String> restaurantCodes = Collections.singletonList("aaa472a3-0544-4e68-a3eb-3740d42ece7d");

        List<SignalHistory> connectionStatusList = Arrays.asList(
                new SignalHistoryBuilder().withReceivedSignal(DateFormatter.format("2019-01-23T20:15")).build(),
                new SignalHistoryBuilder().withReceivedSignal(DateFormatter.format("2019-01-23T20:25")).build()
        );

        when(healthCheckService.fetchSignalHistory(restaurantCodes.get(0), LocalDateTime.now(), LocalDateTime.now().plusHours(1))).thenReturn(connectionStatusList);

        ResponseEntity<List<RestaurantKeepSignalHistoryResponse>> restaurantKeepSignalHistory = restaurantConnectionController.fetchRestaurantHealthHistory(
                restaurantCodes,
                "2019-01-23T20:10",
                "2019-01-23T20:50"
        );

        assertThat(restaurantKeepSignalHistory.getStatusCode(), Matchers.is(HttpStatus.OK));
    }

    @Test
    public void shouldFetchRestaurantHealthHistoryGivenRestaurantAndPeriod() {
        String restaurantCode = "aaa472a3-0544-4e68-a3eb-3740d42ece7d";

        List<SignalHistory> connectionStatusList = Arrays.asList(
                new SignalHistoryBuilder().withReceivedSignal(DateFormatter.format("2019-01-23T20:15")).build(),
                new SignalHistoryBuilder().withReceivedSignal(DateFormatter.format("2019-01-23T20:25")).build()
        );

        when(healthCheckService.fetchSignalHistory(restaurantCode, LocalDateTime.now(), LocalDateTime.now().plusHours(1))).thenReturn(connectionStatusList);

        ResponseEntity<List<SignalHistory>> signalHistory = restaurantConnectionController.fetchRestaurantHealthHistory(
                restaurantCode,
                "2019-01-23T20:10",
                "2019-01-23T20:50"
        );

        assertThat(signalHistory.getStatusCode(), Matchers.is(HttpStatus.OK));
    }
}
