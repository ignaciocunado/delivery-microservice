package nl.tudelft.sem.template.example.service.roles;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Service that authorizes requests from customers.
 */
@Service
public class CustomerService implements RoleService {

    private final transient DeliveryRepository deliveryRepository;

    /**
     * Constructor for the customer controller.
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public CustomerService(DeliveryRepository deliveryRepository) {
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
                .filter(delivery -> delivery.getCustomerID() != null)
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
        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if (fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery del = fetched.get();
        if (body < 0 || body > 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        del.customerRating(body);
        deliveryRepository.save(del);
        return new ResponseEntity<>("200 OK", HttpStatus.OK);
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
