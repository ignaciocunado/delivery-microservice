package nl.tudelft.sem.template.example.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalServiceActual;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class IntegrationTests {
    private static final RestTemplate restTemplate = new RestTemplate();

    private transient ExternalService externalService;

    static String baseUrlOrders = "http://localhost:8083";
    static String baseUrlUsers = "http://localhost:8084";

    static final UUID customerId = UUID.fromString("258dce56-56dc-402c-8fc7-7375d6715b0c");
    static UUID orderId;
    static UUID vendorId;

    @BeforeEach
    void setUp(){
        externalService = new ExternalServiceActual(restTemplate, baseUrlOrders, baseUrlUsers);
    }

    @BeforeAll
    static void setUpMicroservices(){
        String createOrderJson = "{\n" +
                "  \"vendorId\": \"94f95e69-81b7-4c35-932e-9e4baa8dcec2\",\n" +
                "  \"address\": {\n" +
                "    \"houseNumber\": 123,\n" +
                "    \"zip\": \"2133DC\",\n" +
                "    \"longitude\": 40.7128,\n" +
                "    \"latitude\": -74.006\n" +
                "  }\n" +
                "}";

        String createVendorJson = "{\n" +
                "  \"name\": \"vendor4\",\n" +
                "  \"email\": \"user@example.com\",\n" +
                "  \"location\": {\n" +
                "      \"latitude\": 34,\n" +
                "      \"longitude\": 35\n" +
                "    }\n" +
                "}";

        String urlOrders = baseUrlOrders + "/customer/" + customerId + "/order";
        String urlUsers = baseUrlUsers + "/vendors";

        orderId = perfomRequest(createOrderJson, urlOrders, "ID");
        assertNotNull(orderId);
        vendorId = perfomRequest(createVendorJson, urlUsers, "vendorId");
        assertNotNull(vendorId);
    }

    private static UUID perfomRequest(String requestBody, String url, String search){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            return UUID.fromString(getIdFromJson(response.getBody(), search));
        }
        catch (Exception ignored){
            return null;
        }
    }

    private static String getIdFromJson(String body, String search) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);
        JsonNode id = jsonNode.get(search);
        return id.toString().replaceAll("\"", "");
    }

    @Test
    void testGetRestaurantLocation(){
        String restaurantLocation = externalService.getRestaurantLocation(vendorId);

        assertEquals("34.0, 35.0", restaurantLocation);
    }

    @Test
    void testGetRestaurantLocationNull(){
        UUID vendorId = UUID.randomUUID();
        String restaurantLocation = externalService.getRestaurantLocation(vendorId);

        assertNull(restaurantLocation);
    }

    @Test
    void testGetOrderDestination(){
        String orderLocation = externalService.getOrderDestination(customerId, orderId);

        assertEquals("-74.006, 40.7128", orderLocation);
    }

    @Test
    void testGetOrderDestinationNull(){
        UUID customerId = UUID.randomUUID();
        String orderLocation = externalService.getOrderDestination(customerId, orderId);

        assertNull(orderLocation);
    }
}
