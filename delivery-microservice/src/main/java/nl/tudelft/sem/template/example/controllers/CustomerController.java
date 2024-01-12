package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


/**
 * Sub-controller of deliverycontroller.
 */
@Component
public class CustomerController {

    DeliveryRepository deliveryRepository;

    /**
     * Constructor for the customer controller.
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public CustomerController(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Checks whether the role provided is valid.
     * @param role role
     * @return true iff the role is valid
     */
    public boolean checkCustomer(String role) {
        return role.equals("Customer");
    }


    public ResponseEntity<List<UUID>> getAllDeliveriesCustomer(UUID customerID, String role) {
    }
}
