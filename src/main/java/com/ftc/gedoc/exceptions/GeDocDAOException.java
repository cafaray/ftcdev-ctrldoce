package com.ftc.gedoc.exceptions;

public class GeDocDAOException extends Exception {

    public GeDocDAOException() {
    }

    public GeDocDAOException(String message) {
        super(message);
    }

    public GeDocDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeDocDAOException(Throwable cause) {
        super(cause);
    }
    
}
