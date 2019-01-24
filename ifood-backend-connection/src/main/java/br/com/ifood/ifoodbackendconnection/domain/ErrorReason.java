package br.com.ifood.ifoodbackendconnection.domain;

import lombok.Getter;

public enum ErrorReason {

    CREATE_ERROR("It was not possible create a ${entity}"),
    GET_ERROR("It was not possible get a ${entity}"),
    CONNECTION_EVALUATION_ERROR("Not enough connection history found to evaluate.");

    @Getter
    private String messageFormat;

    @Getter
    private boolean blocking;

    ErrorReason(String messageFormat, boolean blocking) {
        this.messageFormat = messageFormat;
        this.blocking = blocking;
    }

    ErrorReason(String messageFormat) { this(messageFormat, true); }
}
