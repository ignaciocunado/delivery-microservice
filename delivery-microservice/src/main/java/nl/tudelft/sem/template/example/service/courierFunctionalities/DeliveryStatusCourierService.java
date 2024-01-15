package nl.tudelft.sem.template.example.service.courierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles courier-accessible methods that involve manipulating the status of a delivery.
 */
@Service
public class DeliveryStatusCourierService {

    /**
     * Interface for manipulating database delivery objects.
     */
    private final transient DeliveryRepository deliveryRepository;

    /**
     * Construct a new DeliveryStatusCourierService.
     * @param deliveryRepository Delivery repository.
     */
    @Autowired
    public DeliveryStatusCourierService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Integrates controller with API for delivered delivery endpoint.
     * @param deliveryId ID of the delivery to mark as delivered. (required)
     * @return courier controller's response entity
     */
    public ResponseEntity<String> deliveredDelivery(UUID deliveryId) {
        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if (fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Delivery d = fetchedDelivery.get();
        d.setStatus("delivered");
        deliveryRepository.save(d);

        return new ResponseEntity<>("Delivery marked as delivered!", HttpStatus.OK);
    }
}
