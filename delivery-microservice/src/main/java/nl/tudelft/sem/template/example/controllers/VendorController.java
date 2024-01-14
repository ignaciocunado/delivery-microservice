package nl.tudelft.sem.template.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.controllers.interfaces.Controller;

import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class VendorController implements Controller {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    /**
     * Constructor for the Vendor Controller.
     * @param restaurantRepository the restaurant repository
     * @param deliveryRepository the delivery repository
     * @param uuidGenerationService the service for generating UUIDs
     */
    @Autowired
    public VendorController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                            UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /** Adds a courier to a restaurant.
     *
     * @param courierId   ID of the courier to add to the restaurant. (required)
     * @param restaurantId ID of the restaurant to add the courier to. (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId) {
        Restaurant r;

        if (restaurantRepository.findById(restaurantId).isPresent()) {
            r = restaurantRepository.findById(restaurantId).get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<UUID> newCouriers = new ArrayList<>(r.getCourierIDs());
        newCouriers.add(courierId);
        r.setCourierIDs(newCouriers);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /** Sets the status to accepted for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId) {
        if (deliveryRepository.findById(deliveryId).isPresent()) {
            Delivery delivery = deliveryRepository.findById(deliveryId).get();
            delivery.setStatus("accepted");
            deliveryRepository.save(delivery);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Gets the pick-up time for a delivery.
     * @param deliveryID UUID of the delivery object
     * @return OffsetDateTime of the estimated time of pick-up
     */
    public ResponseEntity<OffsetDateTime> getPickUpEstimate(UUID deliveryID) {
        Optional<Delivery> estimate = deliveryRepository.findById(deliveryID);
        if (estimate.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OffsetDateTime r = estimate.get().getPickedUpTime();
        if (r == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(r, HttpStatus.OK);
    }



    /** Sets the status to rejected for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> rejectDelivery(UUID deliveryId) {
        if (deliveryRepository.findById(deliveryId).isPresent()) {
            Delivery delivery = deliveryRepository.findById(deliveryId).get();
            delivery.setStatus("rejected");
            deliveryRepository.save(delivery);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Implementation for removing a courier from the database.
     * @param restaurantId ID of the restaurant
     * @return void response entity with HTTP codes
     */
    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId) {
        Optional<Restaurant> fetched = restaurantRepository.findById(restaurantId);
        if (fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Restaurant restaurant = fetched.get();
        List<UUID> couriers = restaurant.getCourierIDs();

        if(!couriers.contains(courierId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        couriers.remove(courierId);

        restaurantRepository.save(restaurant);

        return new ResponseEntity<>(HttpStatus.OK);

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

    /** Gets the list of deliveries for a restaurant.
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param status  The status of the delivery (required) must be 'preparing' or 'given to courier'
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> editStatusDelivery(UUID deliveryId, String status) {
        Optional<Delivery> d = deliveryRepository.findById(deliveryId);
        if (d.isPresent()) {
            if (!status.equals("preparing") && !status.equals("given to courier")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Delivery delivery = d.get();
            delivery.setStatus(status);
            deliveryRepository.save(delivery);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

        // Generate a new ID for the delivery
        final Optional<UUID> newDeliveryId = uuidGenerationService.generateUniqueId(deliveryRepository);
        if (newDeliveryId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // If the given restaurant does not exist, fail.
        if (delivery.getRestaurantID() == null || !restaurantRepository.existsById(delivery.getRestaurantID())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Once we have the new ID - save delivery to the DB.
        delivery.setDeliveryID(newDeliveryId.get());
        Delivery savedDelivery = deliveryRepository.save(delivery);

        final Optional<Delivery> databaseDelivery = deliveryRepository.findById(savedDelivery.getDeliveryID());
        return databaseDelivery.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /**
     * Queries the database for a specific restaurant and throws respective errors.
     * @param restaurantId id of the queried restaurant
     * @return the ResponseEntity containing the status of the request
     */
    public ResponseEntity<String> getRest(UUID restaurantId) {
        Optional<Restaurant> r = restaurantRepository.findById(restaurantId);
        return r.map(restaurant -> new ResponseEntity<>(restaurant.toString(), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>("NOT FOUND \n No restaurant with the given id has been found",
                        HttpStatus.NOT_FOUND));
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

    /**
     * Returns a list of restaurants for a vendor.
     * @param vendorId the vendor to query
     * @return the list of restaurant Ids
     */
    public ResponseEntity<List<UUID>> getVendorRest(UUID vendorId) {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        List<Restaurant> filteredRestaurants = allRestaurants
                .stream()
                .filter(x -> x.getVendorID().equals(vendorId))
                .collect(Collectors.toList());

        if(filteredRestaurants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<UUID> res = new ArrayList<>();

        for (Restaurant r : filteredRestaurants) {
            res.add(r.getRestaurantID());
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
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
        final List<String> allowedRoles = List.of("admin", "vendor");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }
}
