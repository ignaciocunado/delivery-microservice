package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.api.RestaurantApi;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {
    RestaurantRepository restaurantRepository;
    private VendorController vendorController = new VendorController(restaurantRepository);


    @Override
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {

       return vendorController.addCourierToRest(courierId, restaurantId, role);
    }

    public void setVendorController(VendorController vendorController) {
        this.vendorController = vendorController;
    }
}
