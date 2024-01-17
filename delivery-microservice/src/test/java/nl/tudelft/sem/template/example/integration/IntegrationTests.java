package nl.tudelft.sem.template.example.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalServiceActual;
import nl.tudelft.sem.template.example.service.filters.AuthorizationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("integration")
@Disabled
public class IntegrationTests {
    private static final RestTemplate restTemplate = new RestTemplate();

    private transient ExternalService externalService;

    static String baseUrlOrders = "http://localhost:8081";
    static String baseUrlUsers = "http://localhost:8088";

    static final UUID customerId_team14c = UUID.fromString("258dce56-56dc-402c-8fc7-7375d6715b0c");
    static UUID orderId;
    static UUID vendorId;
    static UUID courierId;
    static UUID customerId;

    @Autowired
    private AuthorizationService authorizationService;

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

        String createCourierJson = "{\n" +
                "  \"name\": \"courier\",\n" +
                "  \"email\": \"user@example.com\"\n" +
                "}";

        String createCustomerJson = "{\n" +
                "  \"name\": \"customer\",\n" +
                "  \"email\": \"user@example.com\"\n" +
                "}";

        String urlOrders = baseUrlOrders + "/customer/" + customerId_team14c + "/order";
        String urlVendor = baseUrlUsers + "/vendors";
        String urlCourier = baseUrlUsers + "/couriers";
        String urlCustomer = baseUrlUsers + "/customers";

        orderId = performRequest(createOrderJson, urlOrders, "ID");
        assertNotNull(orderId);
        vendorId = performRequest(createVendorJson, urlVendor, "vendorId");
        assertNotNull(vendorId);
        courierId = performRequest(createCourierJson, urlCourier, "courierId");
        assertNotNull(courierId);
        customerId = performRequest(createCustomerJson, urlCustomer, "customerId");
        assertNotNull(customerId);
    }

    private static UUID performRequest(String requestBody, String url, String search){
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
        String orderLocation = externalService.getOrderDestination(customerId_team14c, orderId);

        assertEquals("-74.006, 40.7128", orderLocation);
    }

    @Test
    void testGetOrderDestinationNull(){
        UUID customerId = UUID.randomUUID();
        String orderLocation = externalService.getOrderDestination(customerId, orderId);

        assertNull(orderLocation);
    }

    @Test
    void testCourierRequest() {
        // 8aad7a16-cc11-4850-b5e9-6a894bbfa48b

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", courierId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "courier");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testAdminRequest() {
        // The admin is generated automatically by user's team each time!
        UUID adminId = UUID.fromString("b38700b9-57a1-40be-ae21-51c4c4628348");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", adminId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "admin");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testCustomerRequest() {
//        b40293f2-aec7-470f-92c7-34b9f6b15ebe
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", customerId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "customer");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testVendorRequest() {
//        e886a65c-b096-4412-8754-929d54bfea89
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", vendorId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "vendor");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testInvalidCourierRequest() {
        UUID userId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "courier");
        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    void testInvalidAdminRequest() {
        UUID userId = UUID.fromString("16e8cb52-fa90-4a1d-bc00-016c18ee63c1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "admin");
        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    void testInvalidCustomerRequest() {
        UUID userId = UUID.fromString("16e8cb52-fa90-4a1d-bc00-016c18ee25c1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "customer");
        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    void testInvalidVendorRequest() {
        UUID userId = UUID.fromString("16e8cb52-fa90-4a1d-bc30-016c18ee65c1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "vendor");
        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    void testInvalidRoleRequest() {
        UUID userId = UUID.fromString("16e8cb52-fa93-4a1d-bc00-016c18ee65c1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "invalid");
        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    void testNoRoleRequest() {
        UUID userId = UUID.fromString("16e8db52-fa90-4a1d-bc00-016c18ee65c1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }
}
