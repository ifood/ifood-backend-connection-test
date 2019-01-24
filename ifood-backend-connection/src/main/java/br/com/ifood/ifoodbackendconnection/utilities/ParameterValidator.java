package br.com.ifood.ifoodbackendconnection.utilities;

import br.com.ifood.ifoodbackendconnection.domain.UnavailabilityReason;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

public class ParameterValidator {

    public static void validate(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate, UnavailabilityReason reason) {
        validate(restaurantCode, startDate, endDate);
        Assert.notNull(reason, "The reason is null");
    }

    public static void validate(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate) {
        validate(restaurantCode);
        Assert.notNull(startDate, "The start date is null");
        Assert.notNull(endDate, "The end date is null");
        Assert.isTrue(startDate.isBefore(endDate), "The start date must be before the end date");
    }

    public static void validate(List<String> codes) {
        Assert.notNull(codes, "The list of restaurants is null");
        Assert.notEmpty(codes, "The list of restaurants is empty");
    }

    public static void validate(String code) {
        Assert.notNull(code, "The restaurant code is null");
    }
}
