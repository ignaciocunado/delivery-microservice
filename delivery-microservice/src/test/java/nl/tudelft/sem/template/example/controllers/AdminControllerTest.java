package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
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

/**
 * Tests functionality of the Admin Controller method implementations.
 */
public class AdminControllerTest {
    private transient AdminController adminController;

    private transient TestRestaurantRepository restaurantRepository;

    private final String role = "admin";

    @BeforeEach
    void setUp() {
        // Set up repositories
        restaurantRepository = new TestRestaurantRepository();

        // Set up controllers
        adminController = new AdminController(restaurantRepository, new UUIDGenerationService());
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
        ResponseEntity<Restaurant> response = adminController.createRestaurant(role, restaurant);
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
        ResponseEntity<Restaurant> firstResponse = adminController.createRestaurant(role, firstRestaurant);
        ResponseEntity<Restaurant> secondResponse = adminController.createRestaurant(role, secondRestaurant);

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
        ResponseEntity<Restaurant> response = adminController.createRestaurant(role, null);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * Only admins should be able to create restaurants.
     */
    @Test
    void testCreateRestaurantWrongRoles() {
        final List<String> rolesToTest = List.of("vendor", "courier", "customer", "sudo", "admi", "v", "c", "a");

        for (final String roleToTest : rolesToTest) {
            Restaurant restaurant = new Restaurant();
            ResponseEntity<Restaurant> response = adminController.createRestaurant(roleToTest, restaurant);

            assertEquals(
                    HttpStatus.UNAUTHORIZED,
                    response.getStatusCode()
            );
        }
    }

    /**
     * An empty role should not allow for restaurant creation.
     */
    @Test
    void testCreateRestaurantNoRole() {
        Restaurant restaurant = new Restaurant();
        ResponseEntity<Restaurant> response = adminController.createRestaurant("", restaurant);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * Tests the case where no more UUIDs are available.
     */
    @Test
    void testCreateRestaurantAllIdsUsed() {
        // We mock the repositories, so we can fake all IDs being taken.
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);
        AdminController localAdminController = new AdminController(
                mockedRestaurantRepository, new UUIDGenerationService()
        );

        // Every single restaurant ID is mapped to this one restaurant
        Restaurant foundRestaurant = new Restaurant();
        Mockito.when(mockedRestaurantRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(foundRestaurant));

        // So, when a new restaurant is created, it should get stuck in a loop and exit!
        final Restaurant restaurantToCreate = new Restaurant();
        ResponseEntity<Restaurant> response = localAdminController.createRestaurant(role, restaurantToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * Saving to the database fails, and returns null. Error must be handled!
     */
    @Test
    void testCreateRestaurantSavingFailed() {
        // We mock the repositories, so we can fake saving failing.
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);
        AdminController localAdminController = new AdminController(
                mockedRestaurantRepository, new UUIDGenerationService()
        );

        // Saving always fails and returns null
        Mockito.when(mockedRestaurantRepository.save(Mockito.any()))
                .thenReturn(null);

        // Ensure a server error occurs
        final Restaurant restaurantToCreate = new Restaurant();
        ResponseEntity<Restaurant> response = localAdminController.createRestaurant(role, restaurantToCreate);

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
        AdminController localAdminController = new AdminController(
                mockedRestaurantRepository, new UUIDGenerationService()
        );

        final Restaurant restaurantToCreate = new Restaurant();
        Mockito.when(mockedRestaurantRepository.save(Mockito.any()))
                .thenReturn(restaurantToCreate);

        // Retrieval always fails and returns empty
        Mockito.when(mockedRestaurantRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        // Ensure a server error occurs
        ResponseEntity<Restaurant> response = localAdminController.createRestaurant(role, restaurantToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }
}
