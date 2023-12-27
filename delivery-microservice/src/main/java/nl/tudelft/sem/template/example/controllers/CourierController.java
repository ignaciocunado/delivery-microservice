package nl.tudelft.sem.template.example.controllers;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Sub controller of DeliveryController. Handles requests from couriers.
 * Note: Remember to define methods here and add them in DeliveryController.
 */
public class CourierController  {

    private boolean checkCourier(String role) {
        return role.equals("courier");
    }

    /**
     * returns the pickup location
     * @param deliveryId
     * @param role
     * @return
     */
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        if (checkCourier(role)) {
            return new ResponseEntity<>("PickUp location is 123.321.666", HttpStatus.OK);
        }
        return new ResponseEntity<>("Authorization failed!", HttpStatus.UNAUTHORIZED);
    }
}
