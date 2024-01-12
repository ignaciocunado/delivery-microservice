package nl.tudelft.sem.template.example.service;

import java.util.UUID;

public class ExternalServiceActual implements ExternalService {
    @Override
    public String getRestaurantLocation(UUID vendorID) {
        // make a request to an external service

    }

    @Override
    public String getOrderDestination(UUID customerId, UUID orderID) {
        return "Destination in format xxx.xxx";
    }
}
