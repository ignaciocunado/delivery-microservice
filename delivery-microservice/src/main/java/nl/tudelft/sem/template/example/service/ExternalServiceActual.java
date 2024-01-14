package nl.tudelft.sem.template.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
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
     * @param userServiceUrl the user service url - mustn't end with "/"!
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

    /**
     * Attempts to contact the 'users' microservice, to verify whether the given user ID
     * has authorization for the given role.
     * @param userId User ID to query.
     * @param role Role to query.
     * @return Whether the user was authorized.
     */
    @Override
    public boolean verify(String userId, String role) {
        // Create URL to contact user microservice
        final Optional<String> url = buildUserAuthorizationQueryURL(userId, role);
        if (url.isEmpty()) {
            return false;
        }

        final int statusCode = performGetRequestToURL(url.get());

        // Print debug info
        System.out.println("\033[96;40m calling users microservice: \033[30;106m " + url + " \033[0m");

        System.out.println("\033[96;40m response status code: \033[30;106m " + statusCode + " \033[0m");

        return statusCode == 200;
    }

    /**
     * Creates a URL that can be used to query the 'user' microservice for authorization.
     * @param userId User ID to query.
     * @param role Role to query.
     * @return URL formatted like 'userServiceUrl/role/userUUID/proof'.
     */
    private Optional<String> buildUserAuthorizationQueryURL(final String userId, final String role) {
        // Ensure the given role actually exists - otherwise the URL would be invalid!
        final List<String> validRoles = List.of("vendor", "courier", "admin", "customer");
        if (!validRoles.contains(role)) {
            return Optional.empty();
        }

        // Construct the URL
        String url = userServiceUrl + "/" + role + "/" + userId + "/proof";
        return Optional.of(url);
    }

    /**
     * Given a URL, perform a GET request and return the response status code (regardless of success).
     * @param url URL to query.
     * @return Response status code.
     */
    private int performGetRequestToURL(final String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
            return response.getStatusCodeValue();
        } catch (RestClientException e) {
            return 401;
        }
    }
}
