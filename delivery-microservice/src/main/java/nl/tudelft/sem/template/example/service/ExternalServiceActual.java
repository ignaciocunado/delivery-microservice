package nl.tudelft.sem.template.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Profile("integration")
public class ExternalServiceActual implements ExternalService {

    private final RestTemplate restTemplate;
    private final String externalServiceUrl;

    @Autowired
    public ExternalServiceActual(RestTemplate restTemplate,
                                 @Value("${external.ordersService.url}") String externalServiceUrl) {
        this.restTemplate = restTemplate;
        this.externalServiceUrl = externalServiceUrl;
    }

    @Override
    public String getRestaurantLocation(UUID vendorID) {
        // For future integration with the orders service,
        // remember to add the API key as a header, etc.
        // This right now is meant to set up the facade structure.

        // header user id injection ...

        String url = externalServiceUrl + "/vendor/" + vendorID + "/location";
        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public String getOrderDestination(UUID customerId, UUID orderID) {
        // For future integration with the orders service,
        // remember to add the API key as a header, etc.
        // This right now is meant to set up the facade structure.

        // header user id injection ...

        String url = externalServiceUrl + "/delivery/" + customerId + "/order/" + orderID + "/destination";
        return restTemplate.getForObject(url, String.class);
    }
}
