package nl.tudelft.sem.template.example.controllers;

import lombok.Setter;
import nl.tudelft.sem.api.RestaurantApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {

    private transient CourierController courierController;
    @Setter
    private transient VendorController vendorController;
    private final transient GlobalController globalController;

    @Autowired
    public RestaurantController(CourierController courierController, VendorController vendorController, GlobalController globalController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
        this.globalController = globalController;
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
     * Calls method in globalController for querying the maximum Delivery zone
     * @param deliveryID ID of the restaurant to query. (required)
     * @param role The role of the user (required)
     * @return the ResponseEntity returned
     */
    @Override
    public ResponseEntity<Double> getMaxDeliveryZone(UUID deliveryID, String role) {
        return globalController.getMaxDeliveryZone(deliveryID, role);
    }
}
