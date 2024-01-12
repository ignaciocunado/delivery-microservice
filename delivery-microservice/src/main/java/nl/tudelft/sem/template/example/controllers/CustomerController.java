package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


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
        return role.equals("customer");
    }

    /**
     * Implementation for the get all deliveries for a customer controller
     * @param customerID id of the customer
     * @param role role of the calling user
     * @return a list containing all deliveries for a customer
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesCustomer(UUID customerID, String role) {
        if(!checkCustomer(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Delivery> fetched = deliveryRepository.findAll();
        List<UUID> deliveries = fetched.stream()
                .filter(delivery -> delivery.getCustomerID().equals(customerID))
                .map(delivery -> delivery.getDeliveryID())
                .collect(Collectors.toList());
        return new ResponseEntity<>(deliveries, HttpStatus.OK);
    }
}
