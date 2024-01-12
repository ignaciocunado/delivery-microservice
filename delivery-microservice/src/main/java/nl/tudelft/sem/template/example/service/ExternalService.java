package nl.tudelft.sem.template.example.service;

import java.util.UUID;

public interface ExternalService {
    String getRestaurantLocation(UUID vendorID);

    String getOrderDestination(UUID customerId, UUID orderID);
}
