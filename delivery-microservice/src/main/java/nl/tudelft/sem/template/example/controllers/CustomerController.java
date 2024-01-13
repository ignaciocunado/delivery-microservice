package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.controllers.interfaces.Controller;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Sub-controller of delivery controller.
 */
@Component
public class CustomerController implements Controller {

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
     * Implementation for the get all deliveries for a customer controller.
     * @param customerID id of the customer
     * @return a list containing all deliveries for a customer
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesCustomer(UUID customerID) {
        List<Delivery> fetched = deliveryRepository.findAll();
        List<UUID> deliveries = fetched.stream()
                .filter(delivery -> delivery.getCustomerID().equals(customerID))
                .map(Delivery::getDeliveryID)
                .collect(Collectors.toList());
        return new ResponseEntity<>(deliveries, HttpStatus.OK);
    }

    /**
     * Rate a delivery.
     * @param deliveryID the delivery to rate
     * @param body the rating to give the delivery
     * @return a response entity with the given rating
     */
    public ResponseEntity<String> setRateOfDelivery(UUID deliveryID, Double body) {
        if (deliveryRepository.existsById(deliveryID)) {
            if (body < 0 || body > 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(deliveryRepository.save(deliveryRepository.findById(deliveryID).get()
                    .customerRating(body)).toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Check the role and handle it further.
     * @param role the role of the user
     * @param operation the method that should be called
     * @param <T> the passed param
     * @return the response type obj
     */
    @Override
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        final List<String> allowedRoles = List.of("admin", "customer");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }
}
