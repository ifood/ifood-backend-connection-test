package br.com.ifood.ifoodbackendconnection.service;

import br.com.ifood.ifoodbackendconnection.domain.*;
import br.com.ifood.ifoodbackendconnection.repository.postgres.RestaurantRepository;
import br.com.ifood.ifoodbackendconnection.repository.postgres.UnavailabilityScheduleRepository;
import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;
import br.com.ifood.ifoodbackendconnection.utilities.Either;
import br.com.ifood.ifoodbackendconnection.utilities.ParameterValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UnavailabilityScheduleService {

    private RestaurantRepository restaurantRepository;
    private UnavailabilityScheduleRepository unavailabilityScheduleRepository;

    @Autowired
    public UnavailabilityScheduleService(RestaurantRepository restaurantRepository,
                                         UnavailabilityScheduleRepository unavailabilityScheduleRepository) {
        this.restaurantRepository = restaurantRepository;
        this.unavailabilityScheduleRepository = unavailabilityScheduleRepository;
    }

    public Either<ErrorList, UnavailabilitySchedule> save(String restaurantCode, UnavailabilityReason reason, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            ParameterValidator.validate(restaurantCode, startDate, endDate, reason);

            Restaurant restaurant = restaurantRepository.findByCode(restaurantCode).orElseThrow(() -> new IllegalArgumentException("Restaurant not found."));

            if (fetchUnavailabilitySchedule(restaurantCode, startDate, endDate).size() > 0){
                throw new IllegalArgumentException("The scheduling intersects with another scheduling previously defined.");
            }

            return  Either.right(unavailabilityScheduleRepository.save(new UnavailabilitySchedule(restaurant, reason.name(), startDate, endDate)));
        } catch (IllegalArgumentException e) {
            ErrorList errorList = ErrorList.withSingleError(ErrorReason.CREATE_ERROR, new HashMap<String, String>() {{
                put("Entity", "Signal");
                put("Reason", e.getMessage());
            }});

            log.error(errorList.toString());
            return Either.left(errorList);
        }
    }

    public List<UnavailabilityScheduleResponse> fetchUnavailabilitySchedule(String restaurantCode,
                                                                            LocalDateTime startDate,
                                                                            LocalDateTime endDate) {
        ParameterValidator.validate(restaurantCode, startDate, endDate);

        restaurantRepository.findByCode(restaurantCode)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Restaurant not found for %s", restaurantCode)));

        return unavailabilityScheduleRepository.fetchUnavailabilitySchedule(restaurantCode)
                .stream()
                .filter(statusScheduleEntity ->
                                        (statusScheduleEntity.getStartDateTime().isAfter(startDate)
                                                    && statusScheduleEntity.getEndDateTime().isBefore(endDate))
                                                || (startDate.isAfter(statusScheduleEntity.getStartDateTime())
                                                    && startDate.isBefore(statusScheduleEntity.getEndDateTime())
                                                || (endDate.isAfter(statusScheduleEntity.getStartDateTime())
                                                    && endDate.isBefore(statusScheduleEntity.getEndDateTime()))))
                .map(UnavailabilityScheduleResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteScheduleBy(String code) {
        Assert.notNull(code, "The schedule code is null");
        unavailabilityScheduleRepository.deleteByScheduleCode(code);
    }
}
