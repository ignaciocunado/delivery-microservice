package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.api.RestaurantApi;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.repositories.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantApi {
    RestaurantRepository restaurantRepository;

    @Override
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId) {

        Restaurant r;

        if(restaurantRepository.findById(restaurantId).isPresent()) {
        r = restaurantRepository.findById(restaurantId).get();}
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
}
