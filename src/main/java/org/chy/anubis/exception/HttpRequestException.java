package org.chy.anubis.exception;

public class HttpRequestException extends RuntimeException{

    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRequestException(String message) {
        super(message);
    }
}
