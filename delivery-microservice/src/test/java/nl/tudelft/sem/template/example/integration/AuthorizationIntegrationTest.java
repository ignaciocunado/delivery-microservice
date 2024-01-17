package nl.tudelft.sem.template.example.integration;

import nl.tudelft.sem.template.example.service.filters.RoleHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthorizationIntegrationTest {

    @LocalServerPort
    private transient int port;

    private WireMockServer wireMockServer;

    private UUID userId;

    @Autowired
    private RoleHandler roleHandler;

    private MockHttpServletRequest request;

    /**
     * Setup tests.
     */
    @BeforeEach
    public void setup() {
        int port = 8081;
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());

        userId = UUID.randomUUID();

        request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testCourierRequest() {
        stubFor(post(urlEqualTo("/couriers/" + userId.toString() + "/proof"))
                .willReturn(aResponse()
                        .withStatus(200)));

        request.addParameter("role", "courier");
        boolean result = roleHandler.handle(request);

        assertTrue(result);
        verify(postRequestedFor(urlEqualTo("/couriers/" + userId.toString() + "/proof")));
    }

    @Test
    void testAdminRequest() {
        stubFor(get(urlEqualTo("/admins/" + userId.toString()))
                .willReturn(aResponse()
                        .withStatus(200)));

        request.addParameter("role", "admin");
        boolean result = roleHandler.handle(request);

        assertTrue(result);
        verify(getRequestedFor(urlEqualTo("/admins/" + userId.toString())));
    }

    @Test
    void testCustomerRequest() {
        stubFor(get(urlEqualTo("/customers/" + userId.toString()))
                .willReturn(aResponse()
                        .withStatus(200)));

        request.addParameter("role", "customer");
        boolean result = roleHandler.handle(request);

        assertTrue(result);
        verify(getRequestedFor(urlEqualTo("/customers/" + userId.toString())));
    }

    @Test
    void testVendorRequest() {
        stubFor(post(urlEqualTo("/vendors/" + userId.toString() + "/proof"))
                .willReturn(aResponse()
                        .withStatus(200)));

        request.addParameter("role", "vendor");
        boolean result = roleHandler.handle(request);

        assertTrue(result);
        verify(postRequestedFor(urlEqualTo("/vendors/" + userId.toString() + "/proof")));
    }

    @Test
    void testInvalidCourierRequest() {
        stubFor(post(urlEqualTo("/couriers/" + userId.toString() + "/proof"))
                .willReturn(aResponse()
                        .withStatus(401)));

        request.addParameter("role", "courier");
        boolean result = roleHandler.handle(request);

        assertFalse(result);
        verify(postRequestedFor(urlEqualTo("/couriers/" + userId.toString() + "/proof")));
    }

    @Test
    void testInvalidAdminRequest() {
        stubFor(get(urlEqualTo("/admins/" + userId.toString()))
                .willReturn(aResponse()
                        .withStatus(401)));

        request.addParameter("role", "admin");
        boolean result = roleHandler.handle(request);

        assertFalse(result);
        verify(getRequestedFor(urlEqualTo("/admins/" + userId.toString())));
    }

    @Test
    void testInvalidCustomerRequest() {
        stubFor(get(urlEqualTo("/customers/" + userId.toString()))
                .willReturn(aResponse()
                        .withStatus(401)));

        request.addParameter("role", "customer");
        boolean result = roleHandler.handle(request);

        assertFalse(result);
        verify(getRequestedFor(urlEqualTo("/customers/" + userId.toString())));
    }

    @Test
    void testInvalidVendorRequest() {
        stubFor(post(urlEqualTo("/vendors/" + userId.toString() + "/proof"))
                .willReturn(aResponse()
                        .withStatus(401)));

        request.addParameter("role", "vendor");
        boolean result = roleHandler.handle(request);

        assertFalse(result);
        verify(postRequestedFor(urlEqualTo("/vendors/" + userId.toString() + "/proof")));
    }

    @Test
    void testInvalidRoleRequest() {
        request.addParameter("role", "invalid");
        boolean result = roleHandler.handle(request);

        assertFalse(result);
    }

    @Test
    void testNoRoleRequest() {
        boolean result = roleHandler.handle(request);

        assertFalse(result);
    }
}
