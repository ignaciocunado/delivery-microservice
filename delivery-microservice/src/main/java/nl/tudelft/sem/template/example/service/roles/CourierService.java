package nl.tudelft.sem.template.example.service.roles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service that authorizes requests from couriers.
 */
@Service
public class CourierService implements RoleService {

    DeliveryRepository deliveryRepository;
    RestaurantRepository restaurantRepository;
    ExternalService externalService;

    /**
     * Constructor for the delivery controller.
     * @param deliveryRepository delivery DB
     * @param restaurantRepository restaurant DB
     * @param externalService external communication
     */
    @Autowired
    public CourierService(DeliveryRepository deliveryRepository, RestaurantRepository restaurantRepository,
                          ExternalService externalService) {
        this.deliveryRepository = deliveryRepository;
        this.restaurantRepository = restaurantRepository;
        this.externalService = externalService;
    }

    /**
     * Returns the pickup location.
     * @param deliveryId id of the delivery
     * @return the pickup location
     */
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId) {
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);
        if(delivery.isEmpty()) {
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Restaurant rest = restaurantRepository.findById(delivery.get().getRestaurantID()).get();

        String location = externalService.getRestaurantLocation(rest.getVendorID());

        return new ResponseEntity<>("location: " + location, HttpStatus.OK);
    }

    /** Returns the destination of the order location.
     *
     * @param deliveryId id of the delivery
     * @return the response Entity
     */
    public ResponseEntity<String> getLocationOfDelivery(UUID deliveryId) {
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);
        if(delivery.isEmpty()) {
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        UUID customerId = delivery.get().getCustomerID();
        UUID orderId = delivery.get().getOrderID();

        String location = externalService.getOrderDestination(customerId, orderId);
        return new ResponseEntity<>("location: " + location, HttpStatus.OK);
    }


    /**
     * Integrates controller with API for delivered delivery endpoint.
     * @param deliveryId ID of the delivery to mark as delivered. (required)
     * @return courier controller's response entity
     */
    public ResponseEntity<String> deliveredDelivery(UUID deliveryId) {
        if (deliveryRepository.findById(deliveryId).isEmpty()) {
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Delivery d = deliveryRepository.findById(deliveryId).get();
        d.setStatus("delivered");
        deliveryRepository.save(d);

        return new ResponseEntity<>("Delivery marked as delivered!", HttpStatus.OK);
    }

    /**
     * Sets the live location of the courier.
     * @param deliveryId the id of the delivery
     * @param body  (optional)
     * @return 200 + message, 403, or 404
     */
    public ResponseEntity<String> setLiveLocation(UUID deliveryId, String body) {
        if(body == null || body.isBlank()) {
            return new ResponseEntity<>("error 400", HttpStatus.BAD_REQUEST);
        }

        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if(fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>("error 404: Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetchedDelivery.get();
        delivery.setLiveLocation(body);
        deliveryRepository.save(delivery);
        return new ResponseEntity<>("200 OK", HttpStatus.OK);
    }

    /**
     * Get the average rating of courier deliveries.
     * @param courierID The ID of the courier to query (required)
     * @return the average rating
     */
    public ResponseEntity<Double> getAvrRating(UUID courierID) {
        List<Delivery> deliveries = deliveryRepository.findAll();
        if (deliveries.isEmpty()) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }

        List<Double> ratingsList = deliveries
                .stream()
                .filter(d -> d.getCourierID().equals(courierID))
                .map(Delivery::getCustomerRating)
                .collect(Collectors.toList());

        if (ratingsList.isEmpty()) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }

        double sumOfRatings = ratingsList.stream().mapToDouble(Double::doubleValue).sum();
        long cnt = ratingsList.size();

        return new ResponseEntity<>(sumOfRatings / cnt, HttpStatus.OK);
    }

    /**
     * Implementation for the get all deliveries for a courier endpoint.
     * @param courierID id of the courier
     * @return a list of IDs of the deliveries for this courier
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesCourier(UUID courierID) {
        List<Delivery> fetched = deliveryRepository.findAll();
        List<UUID> deliveries = fetched.stream()
                .filter(delivery -> delivery.getCourierID().equals(courierID))
                .map(Delivery::getDeliveryID)
                .collect(Collectors.toList());
        return new ResponseEntity<>(deliveries, HttpStatus.OK);
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
        final List<String> allowedRoles = List.of("admin", "courier");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }
}
