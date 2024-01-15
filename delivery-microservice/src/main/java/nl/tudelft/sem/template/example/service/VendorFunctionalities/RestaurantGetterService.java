package nl.tudelft.sem.template.example.service.VendorFunctionalities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
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
public class RestaurantGetterService {

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
    public RestaurantGetterService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                         UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /**
     * Queries the database for a specific restaurant and throws respective errors.
     * @param restaurantId id of the queried restaurant
     * @return the ResponseEntity containing the status of the request
     */
    public ResponseEntity<String> getRest(UUID restaurantId) {
        Optional<Restaurant> fetched = restaurantRepository.findById(restaurantId);
        if(!fetched.isPresent()) {
            return new ResponseEntity<>("NOT FOUND \n No restaurant with the given id has been found",
                    HttpStatus.NOT_FOUND);
        }
        Restaurant r = fetched.get();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(r);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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


}
