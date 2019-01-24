package br.com.ifood.ifoodbackendconnection.domain;

import java.time.LocalDateTime;

public class SignalHistoryBuilder {

    private LocalDateTime receivedSignal;
    private Restaurant restaurant;

    public SignalHistoryBuilder() {
        restaurant = new Restaurant("aaa472a3-0544-4e68-a3eb-3740d42ease7d", "Brew’d Awakening Coffeehaus", null,  null);
    }

    public SignalHistoryBuilder withReceivedSignal(LocalDateTime receivedSignal) {
        this.receivedSignal = receivedSignal;

        return this;
    }

    public SignalHistoryBuilder withRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;

        return this;
    }

    public SignalHistory build() {
        return new SignalHistory(restaurant, receivedSignal);
    }
}
