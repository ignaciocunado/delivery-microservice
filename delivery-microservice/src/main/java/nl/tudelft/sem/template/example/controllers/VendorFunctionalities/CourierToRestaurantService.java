package nl.tudelft.sem.template.example.controllers.VendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourierToRestaurantService {
    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    @Autowired
    public CourierToRestaurantService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
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

    /** Adds a courier to a restaurant.
     * @param courierId   ID of the courier to add to the restaurant. (required)
     * @param restaurantId ID of the restaurant to add the courier to. (required)
     * @param role       The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {

        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (restaurantRepository.findById(restaurantId).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return executeAddCourierToRest(courierId, restaurantId);

    }

    /**
     * private method for executing logic of AddCourierToRest
     * used to lower LOC
     * @param courierId id of courier to query
     * @param restaurantId id of restaurant to query
     * @return corresponding response entity
     */
    private ResponseEntity<Void> executeAddCourierToRest(UUID courierId, UUID restaurantId) {
        Restaurant r;

        r = restaurantRepository.findById(restaurantId).get();
        RestaurantCourierIDsInner curr = new RestaurantCourierIDsInner();
        curr.setCourierID(courierId);

        r.addCourierIDsItem(curr);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Implementation for removing a courier from the database.
     * @param courierId ID of the courier to remove
     * @param restaurantId ID of the restaurant
     * @param role role of the user
     * @return void response entity with HTTP codes
     */
    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Restaurant> rest = restaurantRepository.findById(restaurantId);

        if (rest.isEmpty()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<RestaurantCourierIDsInner> couriers = rest.get().getCourierIDs();

        if (couriers.stream().filter(x -> x.getCourierID().equals(courierId)).collect(Collectors.toList()).isEmpty()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        return checkExistsRemoveCourierRest(rest, couriers, courierId);
    }

    private ResponseEntity<Void> checkExistsRemoveCourierRest(Optional<Restaurant> rest, List<RestaurantCourierIDsInner> couriers, UUID courierId) {


        RestaurantCourierIDsInner toRemove = couriers.stream()
                .filter(x -> x.getCourierID().equals(courierId))
                .collect(Collectors.toList()).get(0);

        couriers.remove(toRemove);

        rest.get().setCourierIDs(couriers);

        restaurantRepository.save(rest.get());

        return new ResponseEntity<Void>(HttpStatus.OK);

    }

}
