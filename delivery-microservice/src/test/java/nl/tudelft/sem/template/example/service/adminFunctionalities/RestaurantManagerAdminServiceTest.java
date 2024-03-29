package nl.tudelft.sem.template.example.service.adminFunctionalities;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.generation.UuidGenerationService;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RestaurantManagerAdminServiceTest {

    private transient TestRestaurantRepository restaurantRepository;

    private transient UuidGenerationService uuidGenerationService;

    private transient RestaurantManagerAdminService sut;

    @BeforeEach
    void setup() {
        // Create repositories
        restaurantRepository = new TestRestaurantRepository();

        // Create services
        uuidGenerationService = new UuidGenerationService();

        sut = new RestaurantManagerAdminService(restaurantRepository, uuidGenerationService);
    }

    /**
     * Good weather case: adding a new restaurant to the database.
     * Only admins should be able to do this.
     */
    @Test
    void testCreateRestaurantGoodWeather() {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID(), UUID.randomUUID(), List.of(), 100.0
        );

        // Check response status
        ResponseEntity<Restaurant> response = sut.createRestaurant(restaurant);
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        // Check saved restaurant content
        restaurant.setRestaurantID(response.getBody().getRestaurantID());
        assertEquals(
                restaurant,
                response.getBody()
        );

        // Ensure we can fetch the new restaurant from the database
        Restaurant restaurantFromRepository = restaurantRepository.findById(response.getBody().getRestaurantID()).get();
        assertEquals(
                restaurant,
                restaurantFromRepository
        );
    }

    /**
     * Save two restaurants with the same ID to the database. This should still give them both unique IDs.
     */
    @Test
    void testCreateRestaurantDoubleId() {
        // Create a new uniquely IDd restaurant
        UUID restaurantId = UUID.randomUUID();
        Restaurant firstRestaurant = new Restaurant(
                restaurantId, UUID.randomUUID(), List.of(), 100.0
        );

        // Create a different restaurant, with that same ID
        Restaurant secondRestaurant = new Restaurant(
                restaurantId, UUID.randomUUID(), List.of(), 50.0
        );

        // Check both response statuses
        ResponseEntity<Restaurant> firstResponse = sut.createRestaurant(firstRestaurant);
        ResponseEntity<Restaurant> secondResponse = sut.createRestaurant(secondRestaurant);

        assertEquals(
                HttpStatus.OK,
                firstResponse.getStatusCode()
        );
        assertEquals(
                HttpStatus.OK,
                secondResponse.getStatusCode()
        );

        // The new restaurants should not have the same ID!
        assertNotEquals(
                firstResponse.getBody().getRestaurantID(),
                secondResponse.getBody().getRestaurantID()
        );

        // Check saved content of both restaurants
        firstRestaurant.setRestaurantID(firstResponse.getBody().getRestaurantID());
        secondRestaurant.setRestaurantID(secondResponse.getBody().getRestaurantID());

        assertEquals(
                firstRestaurant,
                firstResponse.getBody()
        );
        assertEquals(
                secondRestaurant,
                secondResponse.getBody()
        );
    }

    /**
     * Passing a null restaurant should result in a bad request.
     */
    @Test
    void testCreateRestaurantNull() {
        ResponseEntity<Restaurant> response = sut.createRestaurant(null);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    @Test
    void testCreateRestaurantSavingFailedBecauseRestaurantIdIsNull() {
        // We mock the repositories, so we can fake saving failing.
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);
        RestaurantManagerAdminService localSut = new RestaurantManagerAdminService(
                mockedRestaurantRepository, new UuidGenerationService()
        );

        // Saving always fails and returns a restaurant with a null ID
        Mockito.when(mockedRestaurantRepository.save(Mockito.any()))
                .thenReturn(new Restaurant(null, UUID.randomUUID(), List.of(), 100.0));

        // Ensure a server error occurs
        final Restaurant restaurantToCreate = new Restaurant();
        restaurantToCreate.setVendorID(UUID.randomUUID());
        ResponseEntity<Restaurant> response = localSut.createRestaurant(restaurantToCreate);
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * Retrieving the created restaurant from the database fails! Ensure error occurs.
     */
    @Test
    void testCreateRestaurantRetrievalFailed() {
        // We mock the repositories, so we can fake saving failing.
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);

        final Restaurant restaurantToCreate = new Restaurant();
        restaurantToCreate.setVendorID(UUID.randomUUID());
        Mockito.when(mockedRestaurantRepository.save(Mockito.any()))
                .thenReturn(restaurantToCreate);

        // Retrieval always fails and returns empty
        Mockito.when(mockedRestaurantRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        RestaurantManagerAdminService localSut = new RestaurantManagerAdminService(
                mockedRestaurantRepository, new UuidGenerationService()
        );

        // Ensure a server error occurs
        ResponseEntity<Restaurant> response = localSut.createRestaurant(restaurantToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * The vendor ID must be valid for restaurant creation.
     */
    @Test
    void testCreateRestaurantInvalidVendorId() {
        Restaurant restaurantToCreate = new Restaurant();
        restaurantToCreate.setVendorID(null);

        ResponseEntity<Restaurant> response = sut.createRestaurant(restaurantToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }
}
