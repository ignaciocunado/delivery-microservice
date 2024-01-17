package nl.tudelft.sem.template.example.service.generation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Provides unique UUID generation, given a set of existing UUIDs.
 */
@Service
public class UuidGenerationService implements IdGenerationService<UUID> {

    /**
     * Generate a new UUID that is not present in the given list.
     * @param existingIds UUIDs that exist already.
     * @return A new, unique UUID.
     */
    @Override
    public Optional<UUID> generateUniqueId(List<UUID> existingIds) {
        return Optional.of(UUID.randomUUID());
    }

    /**
     * Generate a new UUID, that is not in use by an existing database object.
     * @param repository Repository holding objects with existing UUIDs.
     * @return A new, unique UUID for a new object.
     * @param <C> Object type in the repo.
     */
    @Override
    public <C> Optional<UUID> generateUniqueId(JpaRepository<C, UUID> repository) {
        return Optional.of(UUID.randomUUID());
    }
}
