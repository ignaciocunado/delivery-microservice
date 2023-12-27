package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

// delivery api from generated yaml
import nl.tudelft.sem.api.DeliveryApi;

import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Main Delivery Controller. Calls on other controllers to handle requests.
 * Note: Add methods here to integrate them into the API from other subcontrollers.
 */
@RestController
public class DeliveryController implements DeliveryApi {
    private final transient CourierController courierController;

    /**
     * Constructor for DeliveryController.
     * @param courierController CourierController to handle courier requests
     */
    // tag for dependency injection of courier controller
    @Autowired
    public DeliveryController(CourierController courierController) {
        this.courierController = courierController;
    }
    @Override
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        return courierController.getPickUpLocation(deliveryId, role);
    }
}
