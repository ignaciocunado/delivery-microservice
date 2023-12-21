package nl.tudelft.sem.template.example.controllers;

import java.util.UUID;
import nl.tudelft.sem.api.DeliveryApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourierController implements DeliveryApi {

    private boolean checkCourier(String role) {
        return role.equals("courier");
    }

    @Override
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        if (checkCourier(role)) {
            return new ResponseEntity<>("PickUp location is 123.321.666", HttpStatus.OK);
        }
        return new ResponseEntity<>("Authorization failed!", HttpStatus.UNAUTHORIZED);
    }
}
