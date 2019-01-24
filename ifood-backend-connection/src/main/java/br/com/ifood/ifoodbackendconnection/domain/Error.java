package br.com.ifood.ifoodbackendconnection.domain;

import br.com.ifood.ifoodbackendconnection.utilities.StringSubstitutor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.Map;

public class Error {

    @Getter
    private ErrorReason reason;

    @Getter
    private Map<String, String> messageAttributes;

    public Error(ErrorReason reason, Map<String, String> messageAttributes) {
        this.reason = reason;
        this.messageAttributes = messageAttributes;
    }

    public String getMessage() {
        return StringSubstitutor.replace(reason.getMessageFormat(), messageAttributes);
    }

    @JsonIgnore
    public boolean isNonBlocking() {
        return !isBlocking();
    }

    @JsonIgnore
    public boolean isBlocking() {
        return reason.isBlocking();
    }
}
