package org.trainee.userservice.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(Integer id) {
        super("Record with id=" + id + " wasn't found");
    }
}
