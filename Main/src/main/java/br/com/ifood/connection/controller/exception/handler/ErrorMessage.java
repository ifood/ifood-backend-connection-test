package br.com.ifood.connection.controller.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
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
