package br.com.ifood.ifoodbackendconnection.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class ErrorList {

    @Getter
    @JsonIgnore
    private List<Error> errors;

    public ErrorList(List<Error> errors) {
        this.errors = errors;
    }

    public static ErrorList withSingleError(ErrorReason reason, Map<String, String> mapping) {
        return new ErrorList(singletonList(new Error(reason, mapping)));
    }

    public boolean hasUnrecoverableErrors() {
        return !this.errors.isEmpty() && !getBlockingErrors().isEmpty();
    }

    public boolean hasRecoverableErrors() {
        return !this.errors.isEmpty()
                && !getNonBlockingErrors().isEmpty();
    }

    @JsonProperty("non_blocking")
    public List<Error> getNonBlockingErrors() {
        return errors.stream().filter(Error::isNonBlocking).collect(Collectors.toList());
    }

    @JsonProperty("blocking")
    public List<Error> getBlockingErrors() {
        return errors.stream().filter(Error::isBlocking).collect(Collectors.toList());
    }
}