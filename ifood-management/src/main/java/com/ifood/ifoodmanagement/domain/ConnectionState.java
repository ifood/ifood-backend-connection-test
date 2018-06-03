package com.ifood.ifoodmanagement.domain;

import com.ifood.ifoodmanagement.error.ApiException;

import java.util.Arrays;

public enum ConnectionState {

    OK("ok"),
    AVERAGE("avg"),
    LOW("low");

    public String getState() {
        return state;
    }

    private String state;

    ConnectionState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Connection state: [" + state + "]";
    }

    public static ConnectionState getByType(String type){

        return Arrays.stream(ConnectionState.values())
                .filter(element -> element.getState().equals(type))
                .findFirst()
                .orElseThrow(() -> ApiException.builder()
                        .code(ApiException.VALIDATION_ERROR)
                        .message("No ConnectionState found for given key.")
                        .build());
    }
}
