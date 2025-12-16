package com.example.liferayuserfactory.service;

public class LiferayException extends Exception {
    public LiferayException(String message) {
        super(message);
    }

    public LiferayException(String message, Throwable cause) {
        super(message, cause);
    }
}
