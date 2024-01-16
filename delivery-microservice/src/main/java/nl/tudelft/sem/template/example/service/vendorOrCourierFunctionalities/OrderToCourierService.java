package nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderToCourierService {

    private final transient DeliveryRepository deliveryRepository;

    /**
     * Constructor.
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public OrderToCourierService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Implementation for assign order to courier, modifies delivery object's courier field in the database.
     * @param courierID ID of the courier
     * @param deliveryID ID of the delivery
     * @return ID of the delivery
     */
    public ResponseEntity<UUID> assignOrderToCourier(UUID courierID, UUID deliveryID) {
        Optional<Delivery> fetchedFromDB = deliveryRepository.findById(deliveryID);
        if (fetchedFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetchedFromDB.get();
        delivery.setCourierID(courierID);
        deliveryRepository.save(delivery);
        return new ResponseEntity<>(delivery.getDeliveryID(), HttpStatus.OK);
    }
}
