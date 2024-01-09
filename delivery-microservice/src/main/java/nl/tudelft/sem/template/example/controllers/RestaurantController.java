package nl.tudelft.sem.template.example.controllers;

import lombok.Setter;
import nl.tudelft.sem.api.RestaurantApi;
import nl.tudelft.sem.model.GetVendorRest200ResponseInner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {

    private transient CourierController courierController;
    @Setter
    private transient VendorController vendorController;

    @Autowired
    public RestaurantController(CourierController courierController, VendorController vendorController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
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
     * Calls the method implemented in the vendorController for retrieving a Restaurant
     * @param restaurantId ID of the restaurant to query. (required)
     * @param role The role of the user (required)
     * @return
     */
    @Override
    public ResponseEntity<String> getRest(UUID restaurantId, String role) {
        return vendorController.getRest(restaurantId, role);
    }

}
