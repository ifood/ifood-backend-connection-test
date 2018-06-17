package com.ifood.mqtt.ifoodmqttmanagement.error;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiException extends RuntimeException {

	public static final String VALIDATION_ERROR = "validation_error";
	public static final String INTEGRATION_ERROR = "integration_error";
	public static final String BUSINESS_RULE_ERROR = "business_rule_error";
	public static final String INTERNAL_ERROR = "internal_error";

	private final String code;
	private final String reason;

	@Builder
	public ApiException(String code, String reason, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.reason = reason;
	}

	public ApiException(String code, String msg) {
		super(msg);
		this.code = code;
		this.reason = null;
	}

	public ApiException(String message) {
		super(message);
		this.code = null;
		this.reason = null;
	}
}