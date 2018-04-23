package com.ifood.domain;

public class ConnectionHealthCurrentStatus {

    private final String restaurantCode;
    private final boolean currentHealth;
    private final RestaurantAvailability restaurantAvailability;

    public ConnectionHealthCurrentStatus(String restaurantCode, boolean currentHealth, RestaurantAvailability restaurantAvailability) {
        this.restaurantCode = restaurantCode;
        this.currentHealth = currentHealth;
        this.restaurantAvailability = restaurantAvailability;
    }

    public String getRestaurantCode() {
        return restaurantCode;
    }

    public boolean isOnline(){
        if (!restaurantAvailability.isCurrentlyOpen())
            return false;

        return currentHealth;
    }

}
