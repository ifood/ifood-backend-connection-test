package com.ifood.ifoodmanagement.service.command;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.error.ApiException;
import com.ifood.ifoodmanagement.repository.ClientKeepAliveRepository;
import com.ifood.ifoodmanagement.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ifood.ifoodmanagement.util.IfoodUtil.isRestaurantOnline;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeepAliveCommandService implements IKeepAliveCommandService {

    private final ClientKeepAliveRepository keepAliveRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public void insertClientKeepAliveLog(String restaurantCode) {

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByCode(restaurantCode);

        optionalRestaurant
                .map(restaurant -> {
                    keepAliveRepository.save(
                            ClientKeepAliveLog.builder()
                                    .restaurantCode(restaurant.getCode())
                                    .available(restaurant.isAvailable())
                                    .online(isRestaurantOnline(restaurant))
                                    .build());
                    log.info(String.format("Inserted KeepAlive log for restaurant [%s]", restaurantCode));

                    return restaurant;
                })
                .orElseThrow(() -> ApiException.builder()
                        .code(ApiException.BUSINESS_RULE_ERROR)
                        .message(String.format("No restaurant found for give code %s", restaurantCode))
                        .build());
    }
}
