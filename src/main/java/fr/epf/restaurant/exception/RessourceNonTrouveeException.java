package fr.epf.restaurant.exception;

public class RessourceNonTrouveeException extends RuntimeException {

    public RessourceNonTrouveeException(String message) {
        super(message);
    }
}