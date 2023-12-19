package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

// delivery api from generated yaml
import nl.tudelft.sem.api.DeliveryApi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public class DeliveryController implements DeliveryApi {
    // implement acceptDelivery
    @Override
    public ResponseEntity<Void> acceptDelivery(@Parameter(name = "deliveryID", description = "ID of the delivery to mark as accepted.", required = true, in = ParameterIn.PATH) @PathVariable("deliveryID") UUID deliveryID) {
        // get delivrey by deliveryID

        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }
}
