package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public class VendorController {

    RestaurantRepository restaurantRepository;

    public VendorController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public boolean checkVendor(String role) {
        return role.equals("Vendor");
    }

    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {

        Restaurant r;

        if(!checkVendor(role))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if(restaurantRepository.findById(restaurantId.toString()).isPresent()) {
            r = restaurantRepository.findById(restaurantId.toString()).get();}
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<RestaurantCourierIDsInner> courierList = r.getCourierIDs();
        RestaurantCourierIDsInner curr = new RestaurantCourierIDsInner();
        curr.setCourierID(courierId);

        courierList.add(curr);
        r.setCourierIDs(courierList);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public RestaurantRepository getRestaurantRepository() {
        return restaurantRepository;
    }
}
