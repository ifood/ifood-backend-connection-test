package br.com.ifood.connection.controller.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.ifood.connection.controller.validator.annotation.IdList;

/**
 * @author pnakano
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 09/05/18 13:02
 */
public class IdsListValidator implements ConstraintValidator<IdList, String> {

    @Override
    public void initialize(final IdList constraintAnnotation) {
        System.out.println(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {

        System.out.println(value);
        System.out.println(context.toString());

        return false;
    }
}
