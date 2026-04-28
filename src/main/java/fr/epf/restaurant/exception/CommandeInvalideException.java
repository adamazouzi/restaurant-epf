package fr.epf.restaurant.exception;

public class CommandeInvalideException extends RuntimeException {

    public CommandeInvalideException(String message) {
        super(message);
    }
}