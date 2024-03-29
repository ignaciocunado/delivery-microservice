package nl.tudelft.sem.template.example.service.courierFunctionalities;


import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles courier-accessible methods that involve querying deliveries.
 */
@Service
public class DeliveryGettersCourierService {

    /**
     * Interface for manipulating database delivery objects.
     */
    private final transient DeliveryRepository deliveryRepository;

    /**
     * Construct a new DeliveryGettersCourierService.
     * @param deliveryRepository Delivery repository.
     */
    @Autowired
    public DeliveryGettersCourierService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Get the average rating of courier deliveries.
     * @param courierId The ID of the courier to query (required)
     * @return the average rating
     */
    public ResponseEntity<Double> getAvrRating(UUID courierId) {
        final List<Double> ratingsList = getAllRatingsForCourier(courierId);
        if (ratingsList.isEmpty()) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }

        double sumOfRatings = ratingsList.stream().mapToDouble(Double::doubleValue).sum();
        return new ResponseEntity<>(sumOfRatings / ratingsList.size(), HttpStatus.OK);
    }

    /**
     * Get the entire list of delivery ratings, for one courier.
     * @param courierId ID of courier to query.
     * @return Ratings of all their deliveries.
     */
    private List<Double> getAllRatingsForCourier(final UUID courierId) {
        List<Delivery> deliveries = deliveryRepository.findAll();

        return deliveries.stream()
                .filter(d -> d.getCourierID() != null)
                .filter(d -> d.getCourierID().equals(courierId))
                .map(Delivery::getCustomerRating)
                .collect(Collectors.toList());
    }

    /**
     * Implementation for the get all deliveries for a courier endpoint.
     * @param courierID id of the courier
     * @return a list of IDs of the deliveries for this courier
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesCourier(UUID courierID) {
        List<Delivery> fetched = deliveryRepository.findAll();
        List<UUID> deliveries = fetched.stream()
                .filter(delivery -> delivery.getCourierID() != null)
                .filter(delivery -> delivery.getCourierID().equals(courierID))
                .map(Delivery::getDeliveryID)
                .collect(Collectors.toList());

        return new ResponseEntity<>(deliveries, HttpStatus.OK);
    }
}
