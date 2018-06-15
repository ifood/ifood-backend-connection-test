package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.error.ApiNotFoundException;
import com.ifood.ifoodmanagement.repository.ClientKeepAliveRepository;
import com.ifood.ifoodmanagement.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ifood.ifoodmanagement.util.IfoodUtil.getRandomConnectionState;
import static com.ifood.ifoodmanagement.util.IfoodUtil.isRestaurantOnline;

@Service
@RequiredArgsConstructor
public class RestaurantQueryService implements IRestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final ClientKeepAliveRepository clientKeepAliveRepository;

    @Override
    public Optional<Restaurant> findByCode(String code) {
        return Optional.of(restaurantRepository.findByCode(code))
                .orElseThrow(() -> ApiNotFoundException.builder()
                    .code(ApiNotFoundException.VALIDATION_ERROR)
                    .message(String.format("Could not find restaurant with code", code))
                    .build());
    }

    @Override
    public List<ClientKeepAliveLog> fetchRestaurantAvailabilityHistory(
            String code, boolean available, DateTime from, DateTime to) {

        final Optional<DateTime> start = Optional.ofNullable(from);
        final DateTime end = Optional.ofNullable(to).orElse(DateTime.now());

        if (start.isPresent()){

            final List<ClientKeepAliveLog> keepAliveLogsBetween = clientKeepAliveRepository
                    .findByRestaurantCodeAndAvailableAndLastModifiedBetween(code, available, start.get().toDate(), end.toDate());

            return keepAliveLogsBetween;
        }

        return clientKeepAliveRepository.findByRestaurantCodeAndAvailable(code, available);
    }

    @Override
    @Cacheable(cacheNames = {"restaurants"})
    public List<Restaurant> findAll(){
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> {
                    restaurant.setOnline(isRestaurantOnline(restaurant.isAvailable(), restaurant.getLastModified()));
                    return restaurant;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> fetchRestaurantsOnlineStatus(List<String> restaurantCodes) {

        return restaurantCodes.stream()
                .map(code -> {

                    Optional<Restaurant> optionalRestaurant = restaurantRepository.findByCode(code);

                    if (optionalRestaurant.isPresent()){
                        final boolean isOnline =
                                isRestaurantOnline(optionalRestaurant.get().isAvailable(),
                                                    optionalRestaurant.get().getLastModified());
                        return Restaurant.builder()
                                .code(code)
                                .name(optionalRestaurant.get().getName())
                                .online(isOnline)
                                .connectionStatus(getRandomConnectionState())
                                .build();
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
