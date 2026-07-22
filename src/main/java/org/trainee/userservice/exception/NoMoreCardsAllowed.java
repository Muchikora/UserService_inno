package org.trainee.userservice.exception;

public class NoMoreCardsAllowed extends RuntimeException {
    public NoMoreCardsAllowed() {
        super("User cannot have more than 5 cards");
    }
}
