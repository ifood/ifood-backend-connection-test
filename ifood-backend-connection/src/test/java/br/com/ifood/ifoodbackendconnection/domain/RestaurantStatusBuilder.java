package br.com.ifood.ifoodbackendconnection.domain;

import java.util.ArrayList;
import java.util.List;

public class RestaurantStatusBuilder {
    private List<ConnectionInterval> availableOnline;
    private List<ConnectionInterval> connectionFailed;
    private List<ConnectionInterval> connectionIssuesScheduled;
    private List<ConnectionInterval> businessIssuesScheduled;
    private List<ConnectionInterval> clientAppClosed;

    public RestaurantStatusBuilder() {
        this.availableOnline = new ArrayList<>();
        this.connectionFailed = new ArrayList<>();
        this.connectionIssuesScheduled = new ArrayList<>();
        this.businessIssuesScheduled = new ArrayList<>();
        this.clientAppClosed = new ArrayList<>();
    }

    public RestaurantStatus build() {
        return new RestaurantStatus(availableOnline, connectionFailed, connectionIssuesScheduled, businessIssuesScheduled, clientAppClosed);
    }
}