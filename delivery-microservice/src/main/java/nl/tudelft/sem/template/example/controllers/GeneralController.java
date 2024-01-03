package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GeneralController {

        RestaurantRepository restaurantRepository;
        DeliveryRepository deliveryRepository;

        @Autowired
        public GeneralController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
            this.restaurantRepository = restaurantRepository;
            this.deliveryRepository = deliveryRepository;
        }

        public boolean checkGeneral(String role) {
            return "vendorcouriercustomeradmin".contains(role);
        }


    public ResponseEntity<String> getLiveLocation(UUID deliveryID, String role) {

    }
}
