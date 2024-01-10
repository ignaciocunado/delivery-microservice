package nl.tudelft.sem.template.example.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalService {
    public String getRestaurantLocation(UUID vendorID) {
        return "PickUp in format xxx.xxx";
    }

    public String getOrderDestination(UUID customerId, UUID orderID) {
        return "Destination in format xxx.xxx";
    }
}
