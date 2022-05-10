package org.chy.anubis.exception;

public class FileExecException extends RuntimeException {

    public FileExecException(String message) {
        super(message);
    }

    public FileExecException(String message, Throwable cause) {
        super(message, cause);
    }
}
