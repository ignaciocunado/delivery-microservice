package nl.tudelft.sem.template.example.controllers.VendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeliveryManipulationService {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    @Autowired
    public DeliveryManipulationService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                            UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    public boolean checkVendor(String role) {
        return role.equals("vendor");
    }

    public boolean checkCourier(String role) {
        return role.equals("courier");
    }

    /**
     * Create a new Delivery object in the database. The Delivery is given a new, fully unique ID.
     * @param role Requesting user's role.
     * @param delivery Data of delivery to create. ID is ignored.
     * @return The newly created Delivery object.
     */
    public ResponseEntity<Delivery> createDelivery(String role, Delivery delivery) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final Optional<UUID> newId = uuidGenerationService.generateUniqueId(deliveryRepository);
        if (newId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        delivery.setDeliveryID(newId.get());
        Delivery savedDelivery = deliveryRepository.save(delivery);

        if (savedDelivery == null || savedDelivery.getDeliveryID() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final Optional<Delivery> databaseDelivery = deliveryRepository.findById(savedDelivery.getDeliveryID());
        if (databaseDelivery.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(databaseDelivery.get(), HttpStatus.OK);
    }


    /**
     * Return all deliveries for a given vendor.
     * @param vendorId the id of the vendor to be queried
     * @param role the role of the user calling the endpoint
     * @return all deliveries for the vendor
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesVendor(UUID vendorId, String role) {
        if(!checkVendor(role)) {
            return new ResponseEntity<List<UUID>>(HttpStatus.UNAUTHORIZED);
        }

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
            return new ResponseEntity<List<UUID>>(new ArrayList<UUID>(), HttpStatus.OK);
        }

        return new ResponseEntity<List<UUID>>(filteredDeliveries, HttpStatus.OK);
    }

}
