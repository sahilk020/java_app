package com.crmws.exception;

public class DateValidationException extends Throwable{

	
	private static final long serialVersionUID = 4673730105560516979L;

	public DateValidationException() {
    }

    public DateValidationException(String message) {
        super(message);
    }

    public DateValidationException(Throwable cause) {
        super(cause);
    }

    public DateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
