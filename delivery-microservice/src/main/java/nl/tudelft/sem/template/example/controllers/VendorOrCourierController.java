package nl.tudelft.sem.template.example.controllers;


import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.web.servlet.oauth2.resourceserver.OpaqueTokenDsl;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Handler;

/**
 * Sub-Controller of DeliveryController
 */
@Component
public class VendorOrCourierController {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    /**
     * Constructor
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public VendorOrCourierController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Checks whether the role provided is valid
     * @param role role
     * @return true iff the role is valid
     */
    public boolean checkVendorOrCourier(String role) {
        return "vendorcourier".contains(role);
    }

    /**
     * Implementation of set delivery delay endpoint
     * @param deliveryID id of the delivery to query
     * @param role role of the user
     * @param body new delay to update
     * @return the new delay
     */
    public ResponseEntity<Integer> setDeliveryDelay(UUID deliveryID, String role, Integer body) {
        if(!checkVendorOrCourier(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(body == null || body < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery del = fetched.get();
        del.setDelay(body);
        deliveryRepository.save(del);
        return new ResponseEntity<>(del.getDelay(), HttpStatus.OK);
    }

    /**
     * Implementation of get delivery delay endpoint
     * @param deliveryID id of the delivery
     * @param role role of the user
     * @return delay of the delivery
     */
    public ResponseEntity<Integer> getDeliveryDelay(UUID deliveryID, String role) {
        if(!checkVendorOrCourier(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fetched.get().getDelay(), HttpStatus.OK);
    }

    /**
     * Implementation for assign order to courier.
     * @param courierID ID of the courier
     * @param deliveryID ID of the delivery
     * @param role role of the user
     * @return ID of the delivery
     */
    public ResponseEntity<UUID> assignOrderToCourier(UUID courierID, UUID deliveryID, String role) {
        if(!checkVendorOrCourier(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery del = fetched.get();
        del.setCourierID(courierID);
        deliveryRepository.save(del);
        return new ResponseEntity<>(del.getDeliveryID(), HttpStatus.OK);
    }
}
