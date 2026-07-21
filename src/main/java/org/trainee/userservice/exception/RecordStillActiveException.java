package org.trainee.userservice.exception;

public class RecordStillActiveException extends RuntimeException {
    public RecordStillActiveException(Integer id) {
        super("Record with id=" + id + " is still active. Deactivate it first");
    }
}
