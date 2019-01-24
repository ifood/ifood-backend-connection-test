package br.com.ifood.ifoodbackendconnection.domain;

import br.com.ifood.ifoodbackendconnection.controller.unavailability.response.UnavailabilityScheduleResponse;

import java.util.ArrayList;
import java.util.List;

public class ConnectionStatusBuilder {
    private String restaurantCode;
    private boolean connected;
    private List<UnavailabilityScheduleResponse> unavailabilitySchedule;

    public ConnectionStatusBuilder() {
        connected = true;
        unavailabilitySchedule = new ArrayList<>();
    }

    public ConnectionStatusBuilder withRestaurantCode(String code) {
        this.restaurantCode = code;

        return this;
    }

    public ConnectionStatus build() {
        return new ConnectionStatus(this.restaurantCode, this.connected, this.unavailabilitySchedule);
    }
}
