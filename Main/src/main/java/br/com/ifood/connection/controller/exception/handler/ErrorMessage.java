package br.com.ifood.connection.controller.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Object to hold the error message that gets returned from the controllers
 *
 * @author pnakano
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 09/05/18 18:26
 */
@AllArgsConstructor
@Data
public class ErrorMessage {

    public final Integer code;

    public final String message;

}
