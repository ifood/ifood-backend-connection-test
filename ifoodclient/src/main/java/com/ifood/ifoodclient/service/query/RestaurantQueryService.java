package com.ifood.ifoodclient.service.query;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiNotFoundException;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantQueryService implements IRestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Optional<Restaurant> findByCode(String code) {
        return Optional.of(restaurantRepository.findByCode(code))
                .orElseThrow(() -> ApiNotFoundException.builder()
                    .code(ApiNotFoundException.VALIDATION_ERROR)
                    .message(String.format("Could not find restaurant with code", code))
                    .build());
    }
}
