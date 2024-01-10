package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Setter;
import nl.tudelft.sem.api.RestaurantApi;
import nl.tudelft.sem.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {

    private transient CourierController courierController;
    @Setter
    private transient VendorController vendorController;
    private final transient AdminController adminController;

    @Autowired
    public RestaurantController(CourierController courierController, VendorController vendorController,
                                AdminController adminController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
        this.adminController = adminController;
    }


    @Override
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {
        return vendorController.addCourierToRest(courierId, restaurantId, role);
    }

    @Override
    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId, String role) {
        return vendorController.removeCourierRest(courierId, restaurantId, role);
    }

    /**
     * Integrates controller with API for the create restaurant endpoint.
     * @param role The role of the user (required)
     * @param restaurant Data of the new Restaurant to create. ID is ignored. (required)
     * @return Newly created Restaurant.
     */
    @Override
    public ResponseEntity<Restaurant> createRestaurant(String role, Restaurant restaurant) {
        return adminController.createRestaurant(role, restaurant);
    }
}
