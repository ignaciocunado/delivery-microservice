package nl.tudelft.sem.template.example.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public interface Controller {
    /**
     * Checks if the user is authorized to access the controller.
     * @param role the role of the user
     */
    <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation);
}
