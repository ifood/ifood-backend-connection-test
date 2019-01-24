package br.com.ifood.ifoodbackendconnection.domain;

import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionInterval {
    private final SignalHistory initialKeepAliveSignal;
    private final SignalHistory finalKeepAliveSignal;

    @JsonIgnore
    private ConnectionDefinition connectionDefinition;

    public ConnectionInterval(SignalHistory initialKeepAliveSignal, SignalHistory finalKeepAliveSignal) {
        this.initialKeepAliveSignal = initialKeepAliveSignal;
        this.finalKeepAliveSignal = finalKeepAliveSignal;
    }

    public enum ConnectionDefinition {
        SUCCEEDED,
        FAILED,
        SCHEDULED_CONNECTION_ISSUES,
        SCHEDULED_BUSINESS_ISSUES,
        APP_CLOSED
    }
}
