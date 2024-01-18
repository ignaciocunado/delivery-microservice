package nl.tudelft.sem.template.example.service.vendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.generation.UuidGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryManipulationService {

    private final transient RestaurantRepository restaurantRepository;
    private final transient DeliveryRepository deliveryRepository;
    private final transient UuidGenerationService uuidGenerationService;

    /**
     * Constructor for DeliveryManipulationService.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     * @param uuidGenerationService service to generate unique IDs
     */
    @Autowired
    public DeliveryManipulationService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                            UuidGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /**
     * Create a new Delivery object in the database. The Delivery is given a new, fully unique ID.
     * @param delivery Data of delivery to create. ID is ignored.
     * @return The newly created Delivery object.
     */
    public ResponseEntity<Delivery> createDelivery(Delivery delivery) {
        // Ensure delivery validity
        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // If the given restaurant does not exist, fail.
        if (delivery.getRestaurantID() == null || !restaurantRepository.existsById(delivery.getRestaurantID())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (delivery.getDeliveryID() == null || deliveryRepository.existsById(delivery.getDeliveryID())) {
            delivery.setDeliveryID(UUID.randomUUID());
        }
        Delivery savedDelivery = deliveryRepository.save(delivery);

        final Optional<Delivery> databaseDelivery = deliveryRepository.findById(savedDelivery.getDeliveryID());
        return databaseDelivery.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }


    /**
     * Return all deliveries for a given vendor.
     * @param vendorId the id of the vendor to be queried
     * @return all deliveries for the vendor
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesVendor(UUID vendorId) {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        List<UUID> filteredRestaurants = restaurants.stream().filter(x -> x.getVendorID().equals(vendorId))
                .map(x -> x.getRestaurantID()).collect(Collectors.toList());
        if(filteredRestaurants.isEmpty()) {
            return new ResponseEntity<List<UUID>>(HttpStatus.NOT_FOUND);
        }
        List<Delivery> deliveries = deliveryRepository.findAll();

        List<UUID> filteredDeliveries = deliveries.stream().filter(x -> filteredRestaurants
                .contains(x.getRestaurantID())).map(x -> x.getDeliveryID()).collect(Collectors.toList());

        if(filteredDeliveries.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        return new ResponseEntity<>(filteredDeliveries, HttpStatus.OK);
    }

}
