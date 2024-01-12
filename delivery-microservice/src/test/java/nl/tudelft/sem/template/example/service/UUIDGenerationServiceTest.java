package nl.tudelft.sem.template.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Most parts of the UUID generation service are already implicitly tested through other tests.
 */
public class UUIDGenerationServiceTest {

    private transient UUIDGenerationService sut;

    @BeforeEach
    void setup() {
        sut = new UUIDGenerationService();
    }

    @Test
    void testListGoodWeather() {
        Optional<UUID> Id = sut.generateUniqueId(List.of());

        assertTrue(Id.isPresent());
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
        Optional<UUID> Id = sut.generateUniqueId(existingIds);
        assertTrue(Id.isPresent());
    }

    /**
     * Tests the case where every single ID is taken.
     */
    @Test
    void testListNoIdsLeft() {
        // When the mocked list is queried, the given ID is always present
        List<UUID> existingIds = Mockito.mock(List.class);
        when(existingIds.contains(Mockito.any()))
                .thenReturn(true);

        // Ensure ID generation failed
        Optional<UUID> Id = sut.generateUniqueId(existingIds);
        assertTrue(Id.isEmpty());
    }
}
