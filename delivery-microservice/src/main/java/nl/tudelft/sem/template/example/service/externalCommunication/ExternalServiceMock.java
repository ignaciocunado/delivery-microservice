package nl.tudelft.sem.template.example.service.externalCommunication;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("test")
public class ExternalServiceMock implements ExternalService {
    @Override
    public String getRestaurantLocation(UUID vendorID) {
        return "PickUp in format xxx.xxx";
    }

    @Override
    public String getOrderDestination(UUID customerId, UUID orderID) {
        return "Destination in format xxx.xxx";
    }

    @Override
    public boolean verify(String userId, String role) {
        return true;
    }
}
