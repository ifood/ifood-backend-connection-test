package com.ifood.ifoodclient.domain;

public enum RestaurantStatus {

    AVAILABLE("available"),
    UNAVAILABLE("unavailable");

    private String status;

    public String getStatus() {
        return status;
    }

    RestaurantStatus(String status) {
        this.status = status;
    }
}
