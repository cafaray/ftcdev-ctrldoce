package com.ftc.gedoc.exceptions;

public class GeDocBOException extends Exception {

    public GeDocBOException() {
    }

    public GeDocBOException(String message) {
        super(message);
    }

    public GeDocBOException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeDocBOException(Throwable cause) {
        super(cause);
    }
    
}
