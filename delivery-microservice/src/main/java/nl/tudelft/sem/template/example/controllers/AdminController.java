package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Sub controller that handles requests from admins.
 * Note: Remember to define method implementations here,
 * and call them in view methods such as in RestaurantController.
 */
@Component
public class AdminController {

    /**
     * Holds restaurant database objects.
     */
    RestaurantRepository restaurantRepository;

    /**
     * Generates unique UUIDs for our database. This is injected, instead of being a singleton,
     * so that it can be mocked.
     */
    UUIDGenerationService uuidGenerationService;

    /**
     * Construct a new Admin Controller.
     * @param restaurantRepository Restaurant repository.
     */
    @Autowired
    public AdminController(RestaurantRepository restaurantRepository, UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /**
     * Returns whether the given role has authorization to access admin endpoints.
     * @param role Role to query.
     * @return Whether the role was authorized.
     */
    private boolean checkAdmin(String role) {
        return "admin".equals(role);
    }

    /**
     * Creates and saves a new Restaurant object in the database. A new, unique ID is generated for the new Restaurant.
     * @param role Role of the requesting user.
     * @param restaurant Data of the new Restaurant to create. ID field is ignored.
     * @return The newly created Restaurant.
     */
    public ResponseEntity<Restaurant> createRestaurant(String role, Restaurant restaurant) {
        // Authorize the user
        if (!checkAdmin(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Ensure restaurant validity
        if (restaurant == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Generate a new ID for the restaurant
        final Optional<UUID> newId = uuidGenerationService.generateUniqueId(restaurantRepository);
        if (newId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Once we have the new ID, save the restaurant to the DB.
        restaurant.setRestaurantID(newId.get());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // As an extra layer of internal validation, ensure the newly created restaurant can be fetched from the DB.
        // Failure is considered a server-side error, but the specification does unfortunately not allow this.
        if (savedRestaurant == null || savedRestaurant.getRestaurantID() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final Optional<Restaurant> databaseRestaurant = restaurantRepository
                .findById(savedRestaurant.getRestaurantID());

        if (databaseRestaurant.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(databaseRestaurant.get(), HttpStatus.OK);
    }
}
