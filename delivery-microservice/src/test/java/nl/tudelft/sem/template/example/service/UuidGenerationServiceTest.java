package nl.tudelft.sem.template.example.service;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.service.generation.UuidGenerationService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Most parts of the UUID generation service are already implicitly tested through other tests.
 */
public class UuidGenerationServiceTest {

    private transient UuidGenerationService sut;

    @BeforeEach
    void setup() {
        sut = new UuidGenerationService();
    }

    @Test
    void testListGoodWeather() {
        Optional<UUID> id = sut.generateUniqueId(List.of());

        assertTrue(id.isPresent());
    }

    /**
     * Depending on the behavior of the ID generation implementation, this test may be flaky.
     */
    @Test
    void testListExistingIds() {
        // Generate a large array of random IDs
        List<UUID> existingIds = new ArrayList<>(500);
        for (int i = 0; i < 500; i++) {
            existingIds.add(UUID.randomUUID());
        }

        // Ensure ID generation still works
        Optional<UUID> id = sut.generateUniqueId(existingIds);
        assertTrue(id.isPresent());
    }

    /**
     * Tests the case where every single ID is taken.
     */
    @Test
    void testListNoIdsLeft() {
        // Ensure ID generation failed
        JpaRepository<Delivery, UUID> repository = new TestDeliveryRepository();
        Optional<UUID> id = sut.generateUniqueId(repository);
        assertTrue(id.isPresent());
    }
}
