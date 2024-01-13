package nl.tudelft.sem.template.example.controllers;

import lombok.Setter;
import nl.tudelft.sem.api.RestaurantApi;
import nl.tudelft.sem.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {

    private transient CourierController courierController;
    @Setter
    private transient VendorController vendorController;
    private final transient AdminController adminController;
    private final transient GlobalController globalController;

    /**
     * Constructor for the RestaurantController.
     * @param courierController the courier controller
     * @param vendorController the vendor controller
     * @param adminController the admin controller
     * @param globalController the generic global controller
     */
    @Autowired
    public RestaurantController(CourierController courierController, VendorController vendorController,
                                AdminController adminController, GlobalController globalController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
        this.adminController = adminController;
        this.globalController = globalController;
    }


    @Override
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {
        return vendorController.checkAndHandle(role,
                () -> vendorController.addCourierToRest(courierId, restaurantId));
    }

    @Override
    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId, String role) {
        return vendorController.checkAndHandle(role,
                () -> vendorController.removeCourierRest(courierId, restaurantId));
    }

    /**
     * Integrates controller with API for the create restaurant endpoint.
     * @param role The role of the user (required)
     * @param restaurant Data of the new Restaurant to create. ID is ignored. (required)
     * @return Newly created Restaurant.
     */
    @Override
    public ResponseEntity<Restaurant> createRestaurant(String role, Restaurant restaurant) {
        return adminController.createRestaurant(restaurant);
    }

    /**
     * Calls method in globalController for querying the maximum Delivery zone.
     * @param deliveryID ID of the restaurant to query. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned
     */
    @Override
    public ResponseEntity<Double> getMaxDeliveryZone(UUID deliveryID, String role) {
        return globalController.getMaxDeliveryZone(deliveryID);
    }

    /**
     * Calls the method implemented in the vendorController for retrieving a Restaurant.
     * @param restaurantId ID of the restaurant to query. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned
     */
    @Override
    public ResponseEntity<String> getRest(UUID restaurantId, String role) {
        return vendorController.checkAndHandle(role,
                () -> vendorController.getRest(restaurantId));
    }
}
