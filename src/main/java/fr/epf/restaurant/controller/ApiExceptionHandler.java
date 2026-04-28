package fr.epf.restaurant.controller;

import fr.epf.restaurant.exception.CommandeInvalideException;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.exception.StockInsuffisantException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RessourceNonTrouveeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(RessourceNonTrouveeException ex) {
        return Map.of(
                "status", 404,
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler({
            StockInsuffisantException.class,
            StatutInvalideException.class,
            CommandeInvalideException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return Map.of(
                "status", 400,
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleServerError(Exception ex) {
        return Map.of(
                "status", 500,
                "message", "Erreur interne du serveur"
        );
    }
}