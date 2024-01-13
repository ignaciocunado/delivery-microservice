package nl.tudelft.sem.template.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Profile("integration")
public class ExternalServiceActual implements ExternalService {

    private final RestTemplate restTemplate;
    private final String orderServiceUrl;
    private final String userServiceUrl;

    /**
     * Constructor for the external service integration implementation.
     *
     * @param restTemplate the rest template
     * @param orderServiceUrl the order service url
     * @param userServiceUrl the user service url
     */
    @Autowired
    public ExternalServiceActual(RestTemplate restTemplate,
                                 @Value("${external.ordersService.url}") String orderServiceUrl,
                                 @Value("${external.usersService.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
        this.userServiceUrl = userServiceUrl;
    }

    @Override
    public String getRestaurantLocation(UUID vendorID) {
        // For future integration with the orders service,
        // remember to add the API key as a header, etc.
        // This right now is meant to set up the facade structure.

        // header user id injection ...

        String url = orderServiceUrl + "/vendor/" + vendorID + "/location";
        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public String getOrderDestination(UUID customerId, UUID orderID) {
        // For future integration with the orders service,
        // remember to add the API key as a header, etc.
        // This right now is meant to set up the facade structure.

        // header user id injection ...

        String url = orderServiceUrl + "/delivery/" + customerId + "/order/" + orderID + "/destination";
        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public boolean verify(String userId, String role) {
        StringBuilder url = new StringBuilder(userServiceUrl);

        switch (role) {
            case "vendor":
                url.append("/vendor/");
                break;
            case "courier":
                url.append("/courier/");
                break;
            case "admin":
                url.append("/admin/");
                break;
            case "customer":
                url.append("/customer/");
                break;
            default:
                return false;
        }

        url.append(userId); // user ID
        url.append("/proof"); // proof of role endpoint

        // print the url
        System.out.println("\033[96;40m calling users microservice: \033[30;106m " + url + " \033[0m");

        // get the response entity and check if it is a 200 OK
        // handle the response entity
        int statusCode = 0;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
            statusCode = response.getStatusCodeValue();
        } catch (HttpClientErrorException e) {
            statusCode = e.getRawStatusCode();
        }

        // print the status code
        System.out.println("\033[96;40m response status code: \033[30;106m " + statusCode + " \033[0m");

        return statusCode == 200;
    }
}
