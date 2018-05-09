package br.com.ifood.connection.controller.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.ifood.connection.controller.validator.IdsListValidator;

/**
 * @author pnakano
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 09/05/18 13:05
 */
@Constraint(validatedBy = IdsListValidator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface IdList {

    String message() default "Ids list is invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
