package nl.tudelft.sem.template.example.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Provides unique ID generation, given a set of IDs that already exist.
 * While this service is stateless, its methods are *not* static in order
 * to encourage subclasses' dependency injection.
 *
 * @param <T> Class of ID to generate, e.g. UUID.
 */
public interface IdGenerationService<T> {

    /**
     * Given a set of existing IDs, generate a new random ID that is not in that list.
     *
     * @param existingIds IDs that exist already.
     * @return A new, random, unique ID, or none if generation failed.
     */
    public Optional<T> generateUniqueId(List<T> existingIds);

    /**
     * Given a repository holding objects with existing IDs, generate a new random ID that is not in that list.
     *
     * @param repository Repository holding objects with existing IDs of type T.
     * @param <C> Class of object that the repository holds.
     * @return A new, random, unique ID, or none if generation failed.
     */
    public <C> Optional<T> generateUniqueId(JpaRepository<C, T> repository);
}
