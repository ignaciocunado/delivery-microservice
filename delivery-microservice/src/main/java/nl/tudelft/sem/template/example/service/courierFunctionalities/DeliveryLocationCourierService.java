package nl.tudelft.sem.template.example.service.courierFunctionalities;


import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides courier-accessible methods that interface with a delivery's location fields in some way.
 */
@Service
public class DeliveryLocationCourierService {

    /**
     * Interface for deliveries in the database.
     */
    private final transient DeliveryRepository deliveryRepository;

    /**
     * Interface for restaurants in the database.
     */
    private final transient RestaurantRepository restaurantRepository;

    /**
     * Represents external microservices, such as the 'users' microservice.
     */
    private final transient ExternalService externalService;

    /**
     * Construct a new DeliveryLocationCourierService.
     * @param deliveryRepository Delivery repository.
     */
    @Autowired
    public DeliveryLocationCourierService(DeliveryRepository deliveryRepository,
                                          RestaurantRepository restaurantRepository, ExternalService externalService) {
        this.deliveryRepository = deliveryRepository;
        this.restaurantRepository = restaurantRepository;
        this.externalService = externalService;
    }

    /**
     * Gets a delivery's pick-up location. The location is determined by querying an external service.
     * @param deliveryId Delivery ID to query.
     * @return The queried delivery's pick-up location.
     */
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId) {
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);
        if(delivery.isEmpty()) {
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        // As delivery creation requires a valid restaurant ID, and restaurants
        // cannot be deleted as per the specification, this .get() is safe.
        Restaurant rest = restaurantRepository.findById(delivery.get().getRestaurantID()).get();
        String location = externalService.getRestaurantLocation(rest.getVendorID());
        if (location == null) {
            return new ResponseEntity<>("Location not found!", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("location: " + location, HttpStatus.OK);
    }

    /**
     * Returns the destination of the given delivery, by querying an external service about its associated order.
     * @param deliveryId ID of the delivery.
     * @return The delivery's destination.
     */
    public ResponseEntity<String> getLocationOfDelivery(UUID deliveryId) {
        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if(fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        final Delivery delivery = fetchedDelivery.get();
        String location = externalService.getOrderDestination(delivery.getCustomerID(), delivery.getOrderID());
        if (location == null) {
            return new ResponseEntity<>("Location not found!", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("location: " + location, HttpStatus.OK);
    }

    /**
     * Sets the live location of the courier.
     * @param deliveryId the ID of the delivery.
     * @param body (optional)
     * @return Response status
     */
    public ResponseEntity<String> setLiveLocation(UUID deliveryId, String body) {
        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if(fetchedDelivery.isEmpty() || !validateLocation(body)) {
            return new ResponseEntity<>("404 not found", HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetchedDelivery.get();
        delivery.setLiveLocation(body);
        deliveryRepository.save(delivery);
        return new ResponseEntity<>("200 OK", HttpStatus.OK);
    }

    /**
     * Check whether a given location is valid.
     * @param body Location representation.
     * @return Whether valid.
     */
    private boolean validateLocation(final String body) {
        return body != null && !body.isBlank();
    }
}
