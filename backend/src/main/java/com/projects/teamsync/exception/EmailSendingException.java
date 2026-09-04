package com.projects.teamsync.exception;

public class EmailSendingException
        extends RuntimeException {

    public EmailSendingException(
            String message) {

        super(message);
    }
}