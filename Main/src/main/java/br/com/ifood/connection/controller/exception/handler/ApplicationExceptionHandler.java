package br.com.ifood.connection.controller.exception.handler;

import static br.com.ifood.connection.controller.exception.codes.ErrorCodes.INVALID_CONSTRAINT;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.ifood.connection.controller.validator.annotation.IdList;
import lombok.RequiredArgsConstructor;

/**
 * Handler for all the exceptions
 */
@ControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private static final CharSequence JOINING_CHAR = ",";

    private final MessageSource messageSource;

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ErrorMessage> handleConstraintViolationException(ConstraintViolationException ex) {
        final String collect = ex.getConstraintViolations().stream()
                .map(this::toErrorMessage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(JOINING_CHAR));

        return new ResponseEntity<>(new ErrorMessage(INVALID_CONSTRAINT, collect), HttpStatus.BAD_REQUEST);
    }

    private Optional<String> toErrorMessage(ConstraintViolation<?> violation) {
        final Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
        if (annotation instanceof IdList) {
            return Optional.of(messageSource.getMessage("error.message.idlist-invalid", null, Locale.getDefault()));
        } else if (annotation instanceof Min) {
            return Optional
                    .of(messageSource.getMessage("error.message.restaurantId-invalid", null, Locale.getDefault()));
        }

        return Optional.empty();
    }

}
