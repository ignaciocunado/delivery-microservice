package nl.tudelft.sem.template.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", vendorID.toString());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = userServiceUrl + "/vendors/" + vendorID;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            return getLocationFromJson(response.getBody());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getOrderDestination(UUID customerId, UUID orderId) {
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
        String url = orderServiceUrl + "/customer/" + customerId + "/order/" + orderId;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            return getLocationFromJson(response.getBody());
        } catch (Exception e) {
            return null;
        }
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

    private String getLocationFromJson(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);

        JsonNode locationNode = jsonNode.get("location");
        String latitude = locationNode.get("latitude").toString();
        String longitude = locationNode.get("longitude").toString();
        return latitude + ", " + longitude;
    }
}
