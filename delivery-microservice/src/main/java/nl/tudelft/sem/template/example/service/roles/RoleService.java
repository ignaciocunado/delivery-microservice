package nl.tudelft.sem.template.example.service.roles;

import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public interface RoleService {
    /**
     * Checks if the user is authorized to access actions of a given role.
     * @param role the role of the user
     */
    <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation);
}
