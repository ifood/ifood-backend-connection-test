package br.com.ifood.ifoodbackendconnection.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantStatus {

    private List<ConnectionInterval> availableOnline;
    private List<ConnectionInterval> connectionFailed;
    private List<ConnectionInterval> connectionIssuesScheduled;
    private List<ConnectionInterval> businessIssuesScheduled;
    private List<ConnectionInterval> clientAppClosed;

    public RestaurantStatus() {
        this.availableOnline = new ArrayList<>();
        this.connectionFailed = new ArrayList<>();
        this.connectionIssuesScheduled = new ArrayList<>();
        this.businessIssuesScheduled = new ArrayList<>();
        this.clientAppClosed = new ArrayList<>();
    }
}