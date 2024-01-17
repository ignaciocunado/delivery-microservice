package nl.tudelft.sem.template.example.integration;

import nl.tudelft.sem.template.example.service.filters.AuthorizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
public class AuthorizationIntegrationTest {
    private WireMockServer wireMockServer;

    private UUID userId;

    @Autowired
    private AuthorizationService authorizationService;

    private MockHttpServletRequest request;

    /**
     * Setup tests.
     */
    @BeforeEach
    public void setup() {
        int port = 8088;
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
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
        verify(postRequestedFor(urlEqualTo("/couriers/" + userId.toString() + "/proof")));
    }

    @Test
    void testAdminRequest() {
        UUID userId = UUID.fromString("65c182ce-3b37-409e-b5a9-750a46871c8f");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "admin");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testCustomerRequest() {
        UUID userId = UUID.fromString("469f18d5-0112-4a25-b958-abba0c95cf3b");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "customer");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testVendorRequest() {
//        e886a65c-b096-4412-8754-929d54bfea89
        UUID userId = UUID.fromString("e886a65c-b096-4412-8754-929d54bfea89");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "vendor");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testInvalidCourierRequest() {
        stubFor(post(urlEqualTo("/couriers/" + userId.toString() + "/proof"))
                .willReturn(aResponse()
                        .withStatus(401)));

        request.addParameter("role", "courier");
        boolean result = authorizationService.authorize(request);

        assertFalse(result);
        verify(postRequestedFor(urlEqualTo("/couriers/" + userId.toString() + "/proof")));
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
