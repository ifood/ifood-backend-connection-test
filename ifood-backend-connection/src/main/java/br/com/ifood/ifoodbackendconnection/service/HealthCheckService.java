package br.com.ifood.ifoodbackendconnection.service;

import br.com.ifood.ifoodbackendconnection.domain.*;
import br.com.ifood.ifoodbackendconnection.utilities.Either;
import br.com.ifood.ifoodbackendconnection.utilities.ParameterValidator;
import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;
import br.com.ifood.ifoodbackendconnection.repository.ignite.HealthRepositoryIgnite;
import br.com.ifood.ifoodbackendconnection.repository.postgres.SignalHistoryRepository;
import br.com.ifood.ifoodbackendconnection.repository.postgres.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HealthCheckService {

    private RestaurantRepository restaurantRepository;
    private SignalHistoryRepository signalHistoryRepository;
    private HealthRepositoryIgnite healthRepositoryIgnite;
    private UnavailabilityScheduleService unavailabilityScheduleService;

    @Autowired
    public HealthCheckService(RestaurantRepository restaurantRepository,
                              SignalHistoryRepository signalHistoryRepository,
                              HealthRepositoryIgnite healthRepositoryIgnite,
                              UnavailabilityScheduleService unavailabilityScheduleService) {
        this.signalHistoryRepository = signalHistoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.healthRepositoryIgnite = healthRepositoryIgnite;
        this.unavailabilityScheduleService = unavailabilityScheduleService;
    }

    public Either<ErrorList, SignalHistory> saveSignalForRestaurant(String code) {
        try {
            ParameterValidator.validate(code);

            Restaurant restaurant = restaurantRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant code"));

            Either<ErrorList, SignalHistory> signalHistoryCreated = Either.right(
                    signalHistoryRepository.save(new SignalHistory(restaurant, LocalDateTime.now()))
            );

            healthRepositoryIgnite.save(code, Boolean.TRUE);

            return signalHistoryCreated;
        } catch (IllegalArgumentException e) {
            ErrorList errorList = ErrorList.withSingleError(ErrorReason.CREATE_ERROR, new HashMap<String, String>() {{
                put("Entity", "Signal");
                put("Reason", e.getMessage());
            }});

            log.error(errorList.toString());
            return Either.left(errorList);
        }
    }

    public List<SignalHistory> fetchSignalHistory(final String restaurantCode,
                                                  final LocalDateTime startDate,
                                                  final LocalDateTime endDate) {
        ParameterValidator.validate(restaurantCode, startDate, endDate);

        return signalHistoryRepository.findSignalHistory(restaurantCode)
                .stream()
                .filter(connectionHealthSignal ->
                        connectionHealthSignal.getReceivedSignal().isBefore(endDate) &&
                        connectionHealthSignal.getReceivedSignal().isAfter(startDate)
                )
                .collect(Collectors.toList());
    }


    public List<ConnectionStatus> fetchRestaurantConnectionsFor(final List<String> codes) {
        ParameterValidator.validate(codes);

        final List<ConnectionStatus> connectionHealths = new ArrayList<>();
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime downtimeLimit = LocalDateTime.now().plusMinutes(2);

        codes.forEach(restaurantCode -> {
            boolean signalStatus = healthRepositoryIgnite.exists(restaurantCode);

            List<UnavailabilityScheduleResponse> unavailabilityScheduleResponseList = unavailabilityScheduleService.fetchUnavailabilitySchedule(
                    restaurantCode, currentTime, downtimeLimit
            );

            ConnectionStatus connectionHealth = new ConnectionStatus(
                    restaurantCode, signalStatus, unavailabilityScheduleResponseList
            );
            connectionHealths.add(connectionHealth);
        });

        return connectionHealths;
    }
}
