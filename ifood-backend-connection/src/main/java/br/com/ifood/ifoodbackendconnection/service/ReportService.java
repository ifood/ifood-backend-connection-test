package br.com.ifood.ifoodbackendconnection.service;

import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;
import br.com.ifood.ifoodbackendconnection.domain.ErrorList;
import br.com.ifood.ifoodbackendconnection.domain.ErrorReason;
import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import br.com.ifood.ifoodbackendconnection.utilities.Either;
import br.com.ifood.ifoodbackendconnection.utilities.ParameterValidator;
import br.com.ifood.ifoodbackendconnection.utilities.RestaurantConnectionUtil;
import br.com.ifood.ifoodbackendconnection.domain.ConnectionInterval;
import br.com.ifood.ifoodbackendconnection.domain.RestaurantStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ReportService {

    private HealthCheckService healthCheckService;
    private UnavailabilityScheduleService unavailabilityScheduleService;
    private RestaurantStatusService restaurantStatusService;

    @Autowired
    public ReportService(HealthCheckService healthCheckService,
                         UnavailabilityScheduleService unavailabilityScheduleService,
                         RestaurantStatusService restaurantStatusService) {
        this.healthCheckService = healthCheckService;
        this.unavailabilityScheduleService = unavailabilityScheduleService;
        this.restaurantStatusService = restaurantStatusService;
    }

    public Either<ErrorList, RestaurantStatus> fetchReport(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            ParameterValidator.validate(restaurantCode, startDate, endDate);

            List<SignalHistory> signalHistory = healthCheckService.fetchSignalHistory(restaurantCode, startDate, endDate);

            List<ConnectionInterval> failedConnections = RestaurantConnectionUtil.fetchConnectionsFailures(signalHistory);
            List<ConnectionInterval> successfulConnections = RestaurantConnectionUtil.fetchSuccessfulConnections(signalHistory);

            List<UnavailabilityScheduleResponse> unavailabilityScheduleList = unavailabilityScheduleService.fetchUnavailabilitySchedule(restaurantCode, startDate, endDate);

            return Either.right(
                    restaurantStatusService.fetchRestaurantStatus(unavailabilityScheduleList, failedConnections, successfulConnections)
            );
        } catch (IllegalStateException e){
            ErrorList errorList = ErrorList.withSingleError(ErrorReason.CONNECTION_EVALUATION_ERROR, new HashMap<String, String>() {{
                put("entity", "RestaurantStatus");
            }});

            log.error(errorList.toString());
            return Either.left(errorList);
        }
    }
}
