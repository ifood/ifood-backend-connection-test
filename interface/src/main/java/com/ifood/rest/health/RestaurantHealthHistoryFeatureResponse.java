package com.ifood.rest.health;

import com.ifood.domain.ConnectionHealthSignal;

import java.util.List;

class RestaurantHealthHistoryFeatureResponse {

    private final String code;

    private final List<ConnectionHealthSignal> connections;

    RestaurantHealthHistoryFeatureResponse(String code, List<ConnectionHealthSignal> connectionHealthHistories) {
        this.code = code;
        this.connections = connectionHealthHistories;
    }

    public String getCode() {
        return code;
    }

    public List<ConnectionHealthSignal> getConnections() {
        return connections;
    }
}
