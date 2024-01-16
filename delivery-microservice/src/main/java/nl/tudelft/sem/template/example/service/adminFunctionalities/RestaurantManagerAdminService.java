package nl.tudelft.sem.template.example.service.adminFunctionalities;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.generation.UuidGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


/**
 * Handles implementations of restaurant management endpoints, such as construction.
 */
@Service
public class RestaurantManagerAdminService {

    /**
     * Holds restaurant database objects.
     */
    private final transient RestaurantRepository restaurantRepository;

    /**
     * Generates unique UUIDs for our database. This is injected, instead of being a singleton,
     * so that it can be mocked.
     */
    private final transient UuidGenerationService uuidGenerationService;

    /**
     * Construct a new RestaurantManagerAdminService.
     * @param restaurantRepository Injected repository
     * @param uuidGenerationService Injected UUID generation service
     */
    @Autowired
    public RestaurantManagerAdminService(RestaurantRepository restaurantRepository,
                                         UuidGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /**
     * Implementation of the 'createRestaurant' endpoint.
     * Creates and saves a new Restaurant object in the database. A new, unique ID is generated for the new Restaurant.
     * @param restaurant Data of the new Restaurant to create. ID field is ignored.
     * @return The newly created Restaurant.
     */
    public ResponseEntity<Restaurant> createRestaurant(Restaurant restaurant) {
        // Validate the data, then attempt to create in DB
        if (!validateRestaurantForCreation(restaurant)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Optional<Restaurant> created = tryToCreateRestaurantInDatabase(restaurant);

        // Construct the response
        final HttpStatus status = created.isPresent() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        final Restaurant payload = created.orElse(null);

        return new ResponseEntity<>(payload, status);
    }

    /**
     * Given restaurant data, verify that it is suitable to be created in the database.
     * @param restaurant Restaurant to check. Restaurant ID is ignored.
     * @return Whether to create the restaurant.
     */
    private boolean validateRestaurantForCreation(final Restaurant restaurant) {
        // Pre-condition: restaurant exists
        if (restaurant == null) {
            return false;
        }

        // Check properties
        return restaurant.getVendorID() != null;
    }

    /**
     * Given restaurant data, attempt to directly create a new restaurant in the database (without validation).
     * @param restaurant Restaurant to create.
     * @return Whether creation succeeded.
     */
    private Optional<Restaurant> tryToCreateRestaurantInDatabase(final Restaurant restaurant) {
        // Generate a new ID for the restaurant
        final Optional<UUID> newId = uuidGenerationService.generateUniqueId(restaurantRepository);
        if (newId.isEmpty()) {
            return Optional.empty();
        }

        // Once we have the new ID, save the restaurant to the DB.
        restaurant.setRestaurantID(newId.get());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // We return the DB object in case any properties changed
        return restaurantRepository.findById(savedRestaurant.getRestaurantID());
    }
}
