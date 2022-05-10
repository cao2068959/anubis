package org.chy.anubis.exception;

public class ReflectExecException extends RuntimeException {

    public ReflectExecException(String message) {
        super(message);
    }

    public ReflectExecException(String message, Throwable cause) {
        super(message, cause);
    }
}
