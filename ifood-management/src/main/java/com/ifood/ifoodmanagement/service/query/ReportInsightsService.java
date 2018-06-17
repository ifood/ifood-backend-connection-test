package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.ReportItem;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.domain.RestaurantRating;
import com.ifood.ifoodmanagement.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ReportInsightsService implements IReportInsightsService {

    private final RestaurantQueryService restaurantQueryService;
    private final KeepAliveQueryService keepAliveQueryService;

    @Override
    public ReportItem fetchRestaurantReportInsights(String code) {

        final Optional<Restaurant> optionalRestaurant = restaurantQueryService.findByCode(code);

        return optionalRestaurant
                .map(this::proccessRestaurantHistoricData)
                .orElseThrow(() -> ApiException
                    .builder()
                    .code(ApiException.BUSINESS_RULE_ERROR)
                    .message("No restaurant found for given code: " + code)
                    .build());
    }

    private ReportItem proccessRestaurantHistoricData(Restaurant restaurant){

        List<ClientKeepAliveLog> keepAliveLogs =
                keepAliveQueryService.fetchAllByCode(restaurant.getCode());

        if (keepAliveLogs.isEmpty()){
            return ReportItem.builder()
                    .code(restaurant.getCode())
                    .name(restaurant.getName())
                    .finalDescription("No data found for restaurant [" + restaurant.getCode() + "]")
                    .build();
        }

        final Double avgOnline = getFilterAverage(getFilter(true), keepAliveLogs);
        final Double avgOffline = 1 - avgOnline;
        final Double avgAvailable = getFilterAverage(getFilter(false), keepAliveLogs);
        final RestaurantRating rating = RestaurantRating.getRating(avgOffline);

        final ReportItem reportItem = ReportItem.builder()
                .code(restaurant.getCode())
                .name(restaurant.getName())
                .stars(restaurant.getStars())
                .avgOnline(avgOnline)
                .avgOffline(avgOffline)
                .offlineRating(rating)
                .availableRating(avgAvailable)
                .finalDescription(rating.getRatingDescription())
                .build();

        return reportItem;
    }

    private Predicate getFilter(boolean isOfflineRating){

        final Predicate<ClientKeepAliveLog> isOnlineFilter = ClientKeepAliveLog::isOnline;
        final Predicate<ClientKeepAliveLog> isAvailableFilter = ClientKeepAliveLog::isAvailable;

        return isOfflineRating ? isOnlineFilter : isAvailableFilter;
    }

    private Double getFilterAverage(Predicate<ClientKeepAliveLog> filter,
                                    List<ClientKeepAliveLog> keepAliveLogs){

        final long onlineCount = keepAliveLogs
                .stream()
                .filter(filter)
                .count();

        return Double.valueOf(onlineCount/keepAliveLogs.size());
    }
}
