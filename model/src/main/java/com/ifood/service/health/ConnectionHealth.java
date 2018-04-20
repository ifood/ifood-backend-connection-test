package com.ifood.service.health;

import com.ifood.domain.RestaurantAvailability;

public class ConnectionHealth {

    private final String restaurantCode;
    private final boolean currentHealth;
    private final RestaurantAvailability restaurantAvailability;

    ConnectionHealth(String restaurantCode, boolean currentHealth, RestaurantAvailability restaurantAvailability) {
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
