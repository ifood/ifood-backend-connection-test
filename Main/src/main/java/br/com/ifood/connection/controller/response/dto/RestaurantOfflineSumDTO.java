package br.com.ifood.connection.controller.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantOfflineSumDTO {

    private String restaurantName;

    private Long offlineSum;

    public static int compare(RestaurantOfflineSumDTO o1, RestaurantOfflineSumDTO o2) {
        return o1.offlineSum.compareTo(o2.offlineSum);
    }
}
