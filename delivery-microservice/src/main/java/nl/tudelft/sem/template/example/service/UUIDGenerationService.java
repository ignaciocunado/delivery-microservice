package nl.tudelft.sem.template.example.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Provides unique UUID generation, given a set of existing UUIDs.
 */
@Service
public class UUIDGenerationService implements IdGenerationService<UUID> {

    /**
     * Generate a new UUID that is not present in the given list.
     * @param existingIds UUIDs that exist already.
     * @return A new, unique UUID.
     */
    @Override
    public Optional<UUID> generateUniqueId(List<UUID> existingIds) {
        // Since list lookups are (assumed to be) cheap, the generation iteration limit is high.
        final int maxNewIdGenerationAttempts = 500;
        int newIdGenerationAttempts = 0;
        UUID newId;

        // Generate a unique ID, or fail after max iteration count
        do {
            newId = UUID.randomUUID();
            newIdGenerationAttempts += 1;
        } while (existingIds.contains(newId) && newIdGenerationAttempts < maxNewIdGenerationAttempts);

        // Check result - did we succeed?
        if (!existingIds.contains(newId)) {
            return Optional.of(newId);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Generate a new UUID, that is not in use by an existing database object.
     * @param repository Repository holding objects with existing UUIDs.
     * @return A new, unique UUID for a new object.
     * @param <C> Object type in the repo.
     */
    @Override
    public <C> Optional<UUID> generateUniqueId(JpaRepository<C, UUID> repository) {
        // Since database lookups could be expensive, the generation iteration limit is low.
        final int maxNewIdGenerationAttempts = 10;
        int newIdGenerationAttempts = 0;
        UUID newId;

        // Generate a unique ID, or fail after max iteration count
        do {
            newId = UUID.randomUUID();
            newIdGenerationAttempts += 1;
        } while (repository.findById(newId).isPresent() && newIdGenerationAttempts < maxNewIdGenerationAttempts);

        // Check result - did we succeed?
        if (!repository.findById(newId).isPresent()) {
            return Optional.of(newId);
        } else {
            return Optional.empty();
        }
    }
}
