package nl.tudelft.sem.template.example.service.courierFunctionalities;


import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles courier-accessible methods that involve querying deliveries.
 */
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
}
