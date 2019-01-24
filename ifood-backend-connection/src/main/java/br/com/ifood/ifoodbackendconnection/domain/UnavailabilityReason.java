package br.com.ifood.ifoodbackendconnection.domain;

public enum UnavailabilityReason {
    DELIVERY_STACK_LACK ("Lack of delivery staff"),
    CONNECTION_ISSUES("Connection issues (bad internet)"),
    OVERLOADED("Overloaded due to offline orders"),
    HOLIDAYS("Holidays");

    private final String description;

    UnavailabilityReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}