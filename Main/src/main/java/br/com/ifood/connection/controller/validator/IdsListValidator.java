package br.com.ifood.connection.controller.validator;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.ifood.connection.controller.validator.annotation.IdList;

/**
 * Validates the list of ids that gets passed as a String parameter to the controller.<br/>
 * It also validates if the ids are positive and different from zero.
 *
 * @author pnakano
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 09/05/18 13:02
 */
public class IdsListValidator implements ConstraintValidator<IdList, String> {

    private static final String IDS_LIST_REGEXP = "( *\\d+ *)(, *\\d+ *)*";

    private final Pattern idListPattern = Pattern.compile(IDS_LIST_REGEXP);

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {

        boolean isPatternOk = idListPattern.matcher(value).matches();
        if (!isPatternOk) {
            return false;
        }

        return Stream.of(splitIdsList(value))
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .noneMatch(this::equalOrLessThanZero);

    }

    private String[] splitIdsList(String idsList) {
        return idsList.split(",");
    }

    private boolean equalOrLessThanZero(Long value) {
        return value <= 0L;
    }
}
