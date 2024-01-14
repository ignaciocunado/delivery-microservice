package nl.tudelft.sem.template.example.controllers;

import lombok.Setter;
import nl.tudelft.sem.api.RestaurantApi;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.roles.AdminService;
import nl.tudelft.sem.template.example.service.roles.CourierService;
import nl.tudelft.sem.template.example.service.roles.GlobalService;
import nl.tudelft.sem.template.example.service.roles.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {

    private transient CourierService courierService;
    @Setter
    private transient VendorService vendorService;
    private final transient AdminService adminService;
    private final transient GlobalService globalService;

    /**
     * Constructor for the RestaurantController.
     * @param courierService the courier controller
     * @param vendorService the vendor controller
     * @param adminService the admin controller
     * @param globalService the generic global controller
     */
    @Autowired
    public RestaurantController(CourierService courierService, VendorService vendorService,
                                AdminService adminService, GlobalService globalService) {
        this.courierService = courierService;
        this.vendorService = vendorService;
        this.adminService = adminService;
        this.globalService = globalService;
    }

    /**
     * Integrates controller with API for the add courier to restaurant endpoint.
     * @param restaurantId ID of the restaurant to modify. (required)
     * @param courierId ID of the courier to add. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned by the method.
     */
    @Override
    public ResponseEntity<Void> addCourierToRest(UUID restaurantId, UUID courierId, String role) {
        return vendorService.checkAndHandle(role,
                () -> vendorService.addCourierToRest(courierId, restaurantId));
    }

    /**
     * Integrates controller with API for the remove courier from restaurant endpoint.
     * @param restaurantId ID of the restaurant to modify. (required)
     * @param courierId ID of the courier to remove. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned by the method.
     */
    @Override
    public ResponseEntity<Void> removeCourierRest(UUID restaurantId, UUID courierId, String role) {
        return vendorService.checkAndHandle(role,
                () -> vendorService.removeCourierRest(courierId, restaurantId));
    }

    /**
     * Integrates controller with API for the create restaurant endpoint.
     * @param role The role of the user (required)
     * @param restaurant Data of the new Restaurant to create. ID is ignored. (required)
     * @return Newly created Restaurant.
     */
    @Override
    public ResponseEntity<Restaurant> createRestaurant(String role, Restaurant restaurant) {
        return adminService.checkAndHandle(role,
                () -> adminService.createRestaurant(restaurant));
    }

    /**
     * Calls method in globalService for querying the maximum Delivery zone.
     * @param deliveryID ID of the restaurant to query. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned
     */
    @Override
    public ResponseEntity<Double> getMaxDeliveryZone(UUID deliveryID, String role) {
        return globalService.getMaxDeliveryZone(deliveryID);
    }

    /**
     * Calls the method implemented in the vendorService for retrieving a Restaurant.
     * @param restaurantId ID of the restaurant to query. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned
     */
    @Override
    public ResponseEntity<String> getRest(UUID restaurantId, String role) {
        return vendorService.checkAndHandle(role,
                () -> vendorService.getRest(restaurantId));
    }

    /**
     * Calls the corresponding method in the globalService.
     * @param restaurantId ID of the restaurant to modify. (required)
     * @param role The role of the user (required)
     * @param body Leave the delivery zone blank to reset to the default value instead. (optional)
     * @return the ResponseEntity returned by the method.
     */
    @Override
    public ResponseEntity<Void> setMaxDeliveryZone(UUID restaurantId, String role, Double body) {
        return globalService.setMaxDeliveryZone(restaurantId, body);
    }

    /**
     * Calls the method implemented in the vendorService for retrieving a list of restaurants.
     * @param vendorId ID of the vendor to query. (required)
     * @param role The role of the user (required)
     * @return the list of restaurants
     */
    @Override
    public ResponseEntity<List<UUID>> getVendorRest(UUID vendorId, String role) {
        return vendorService.checkAndHandle(role,
                () -> vendorService.getVendorRest(vendorId));
    }
}
