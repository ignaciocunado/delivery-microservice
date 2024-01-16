package nl.tudelft.sem.template.example.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalServiceActual;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ExternalServiceWireMockTest {
    private RestTemplate restTemplate = new RestTemplate();

    public static WireMockServer wireMockServer;

    private transient ExternalService externalService;

    @BeforeAll
    static void startWireServer(){
        int port = 8088;
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
        configureFor("localhost", port);
    }

    @BeforeEach
    void setUp(){
        externalService = new ExternalServiceActual(restTemplate, wireMockServer.baseUrl(), wireMockServer.baseUrl());
    }

    @AfterAll
    static void stopWireServer(){
        wireMockServer.stop();
    }

    @Test
    void testWireMock(){
        assertTrue(wireMockServer.isRunning());
    }

    @Test
    void testGetRestaurantLocation(){
        UUID vendorId = UUID.randomUUID();
        wireMockServer.stubFor(
                WireMock.get("/vendors/" + vendorId)
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        "{\n" +
                                                "  \"vendorId\": \"908ads99-sd8sad8-sdasda88-dasdad9\",\n" +
                                                "  \"name\": \"Arthur Dent\",\n" +
                                                "  \"isBlocked\": true,\n" +
                                                "  \"email\": \"user@example.com\",\n" +
                                                "  \"isApproved\": false,\n" +
                                                "  \"location\": {\n" +
                                                "    \"id\": \"908ads99-sd8sad8-sdasda88-dasdad9\",\n" +
                                                "    \"latitude\": 34.092,\n" +
                                                "    \"longitude\": 34.092,\n" +
                                                "    \"zipCode\": \"2554EZ\",\n" +
                                                "    \"houseNumber\": 24\n" +
                                                "  },\n" +
                                                "  \"schedule\": {\n" +
                                                "    \"id\": \"908ads99-sd8sad8-sdasda88-dasdad9\",\n" +
                                                "    \"schedule\": \"10:00-20:00,10:00-20:00\"\n" +
                                                "  }\n" +
                                                "}")));
        String restaurantLocation = externalService.getRestaurantLocation(vendorId);

        assertEquals("34.092, 34.092", restaurantLocation);
        WireMock.verify(exactly(1),
                WireMock.getRequestedFor(urlEqualTo("/vendors/" + vendorId))
                        .withHeader("X-User-Id", equalTo(vendorId.toString())));
    }

    @Test
    void testGetRestaurantLocationNull(){
        UUID vendorId = UUID.randomUUID();
        wireMockServer.stubFor(
                WireMock.get("/vendors/" + vendorId)
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        "{\"httpCode\": 403, \"message\": " + "\"You do not have the right permissions to access this resource.\"}")));
        String restaurantLocation = externalService.getRestaurantLocation(vendorId);

        assertNull(restaurantLocation);
        WireMock.verify(exactly(1),
                WireMock.getRequestedFor(urlEqualTo("/vendors/" + vendorId))
                        .withHeader("X-User-Id", equalTo(vendorId.toString())));
    }

    @Test
    void testGetRestaurantLocationNull404(){
        UUID vendorId = UUID.randomUUID();
        wireMockServer.stubFor(
                WireMock.get("/vendors/" + vendorId)
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        "{\"httpCode\": 404, \"message\": " + "\"You do not have the right permissions to access this resource.\"}")));
        String restaurantLocation = externalService.getRestaurantLocation(vendorId);

        assertNull(restaurantLocation);
        WireMock.verify(exactly(1),
                WireMock.getRequestedFor(urlEqualTo("/vendors/" + vendorId))
                        .withHeader("X-User-Id", equalTo(vendorId.toString())));
    }

    @Test
    void testGetOrderDestination(){
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        wireMockServer.stubFor(
                WireMock.get("/customer/" + customerId + "/order/" + orderId)
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ \"ID\": \"987e4567-e89b-12d3-a456-423144174001\", \"dishes\": [{ \"ID\": \"ccf1f2d7-8f7f-4044-9a30-16790a432977\", \"dish\": { \"ID\": \"eef1f2d7-8f7f-4044-9a30-16790a432977\", \"name\": \"Pizza Margherita\", \"imageLink\": \"http://example.com/images/pizza.jpg\", \"price\": 12.5, \"allergens\": [ \"Gluten\", \"Dairy\" ], \"ingredients\": [ \"Flour\", \"Tomatoes\", \"Mozzarella\", \"Basil\", \"Olive Oil\" ], \"description\": \"Best pizza in Europe\" }, \"quantity\": 1 } ], \"location\": { \"houseNumber\": 456, \"zip\": \"12346\", \"longitude\": 40.713, \"latitude\": -74.007 }, \"specialRequirements\": \"Extra cheese on pizza\", \"status\": \"preparing\", \"totalPrice\": 22.5, \"vendorId\": 42, \"customerId\": \"123e4567-e89b-12d3-a456-426614174000\", \"orderTime\": \"2023-12-07T15:30:00Z\" }")));
        String orderLocation = externalService.getOrderDestination(customerId, orderId);

        assertEquals("-74.007, 40.713", orderLocation);
        WireMock.verify(exactly(1),
                WireMock.getRequestedFor(urlEqualTo("/customer/" + customerId + "/order/" + orderId)));
    }

    @Test
    void testGetOrderDestinationNull(){
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        wireMockServer.stubFor(
                WireMock.get("/customer/" + customerId + "/order/" + orderId)
                        .willReturn(aResponse()
                                .withStatus(400)));
        String orderLocation = externalService.getOrderDestination(customerId, orderId);

        assertNull(orderLocation);
        WireMock.verify(exactly(1),
                WireMock.getRequestedFor(urlEqualTo("/customer/" + customerId + "/order/" + orderId)));
    }
}
