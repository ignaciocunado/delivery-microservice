package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.api.DeliveryApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CourierController implements DeliveryApi {
    @Override
    public ResponseEntity<String> getPickUpDelivery(@PathVariable("deliveryID") UUID id) {

        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
