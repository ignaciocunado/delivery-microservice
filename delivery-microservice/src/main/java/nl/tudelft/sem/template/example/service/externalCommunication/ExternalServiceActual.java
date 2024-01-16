package nl.tudelft.sem.template.example.service.externalCommunication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Profile("integration")
public class ExternalServiceActual implements ExternalService {

    private final transient RestTemplate restTemplate;
    private final transient String orderServiceUrl;
    private final transient String userServiceUrl;

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
        return switch (role) {
            case "vendor", "courier" -> verifyWithProof(userId, role);
            case "admin", "customer" -> verifyWithGetter(userId, role);
            default -> false;
        };
    }


    /**
     * Verifies using User microservice's proof endpoint.
     *
     * @param userId User ID to query.
     * @param role Role to query.
     * @return Whether the user was authorized.
     */
    private boolean verifyWithProof(String userId, String role) {
        // Create URL to contact user microservice
        String url = userServiceUrl + "/" + role + "s/" + userId + "/proof";

        final int statusCode = performRequest(url, userId, HttpMethod.POST);

        return statusCode == 200;
    }

    /**
     * Verifies using User microservice's getter endpoint.
     *
     * @param userId User ID to query.
     * @param role  Role to query.
     * @return Whether the user was authorized.
     */
    private boolean verifyWithGetter(String userId, String role) {
        // Create URL to contact user microservice
        String url = userServiceUrl + "/" + role + "s/" + userId;

        final int statusCode = performRequest(url, userId, HttpMethod.GET);

        return statusCode == 200;
    }


    /**
     * Performs request to the given URL with given method.
     *
     * @param url URL to query.
     * @param userId User ID to query.
     * @return Response status code.
     */
    private int performRequest(final String url, final String userId, final HttpMethod method) {
        System.out.println("\033[96;40m calling users microservice: \033[30;106m " + url + " \033[0m");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", userId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(userId, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    method,
                    requestEntity,
                    String.class
            );
            System.out.println("\033[96;40m authorized \033[0m");
            return response.getStatusCodeValue();
        } catch (RestClientException e) {
            System.out.println("\033[96;40m unauthorized \033[0m");
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
