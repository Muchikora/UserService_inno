package org.trainee.userservice.exception;

public class RecordStillActiveException extends RuntimeException {
    public RecordStillActiveException(Long id) {
        super("Record with id=" + id + " is still active. Deactivate it first");
    }
}
