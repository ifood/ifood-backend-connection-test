package com.ifood.database.entity;

import java.time.LocalDateTime;

public class ConnectionHealthSignalEntity {

    private final String code;
    private final LocalDateTime receivedAt;

    public ConnectionHealthSignalEntity(String code, LocalDateTime receivedAt) {
        this.code = code;
        this.receivedAt = receivedAt;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }
}
