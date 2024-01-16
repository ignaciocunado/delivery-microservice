package nl.tudelft.sem.template.example.service.vendorFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.generation.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryIdGetterService {


    private final transient RestaurantRepository restaurantRepository;
    private final transient DeliveryRepository deliveryRepository;
    private final transient UUIDGenerationService uuidGenerationService;

    /**
     * Constructor for the Vendor Controller.
     * @param restaurantRepository the restaurant repository
     * @param deliveryRepository the delivery repository
     * @param uuidGenerationService the service for generating UUIDs
     */
    @Autowired
    public DeliveryIdGetterService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                                   UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /**
     * Implementation for the get customer ID endpoint.
     * @param deliveryID id of the delivery
     * @return id of customer
     */
    public ResponseEntity<UUID> getCustomerByDeliveryId(UUID deliveryID) {
        final Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fetched.get().getCustomerID(), HttpStatus.OK);
    }

    /**
     * Get the courierId of the delivery.
     * @param deliveryId the id of delivery
     * @return the UUID in the response entity
     */
    public ResponseEntity<UUID> getCourierIdByDelivery(UUID deliveryId) {
        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if (fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fetchedDelivery.get().getCourierID(), HttpStatus.OK);
    }



}
