package com.ifood.ifoodmanagement.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportItem {

    private String code;

    private String name;

    private Integer stars;

    private Double avgOnline;

    private Double avgOffline;

    private RestaurantRating offlineRating;

    private Double availableRating;

    private String finalDescription;
}
