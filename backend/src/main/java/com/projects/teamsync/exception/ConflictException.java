package com.projects.teamsync.exception;

public class ConflictException
        extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}