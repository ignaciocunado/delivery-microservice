package nl.tudelft.sem.template.example.integration;

import nl.tudelft.sem.template.example.service.filters.AuthorizationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("integration")
@SpringBootTest
public class AuthorizationIntegrationTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Test
    void testCourierRequest() {
//        8aad7a16-cc11-4850-b5e9-6a894bbfa48b
        UUID userId = UUID.fromString("8aad7a16-cc11-4850-b5e9-6a894bbfa48b");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.setRequestURI("/anywhere");

        request.addParameter("role", "courier");
        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    void testAdminRequest() {
//        70d37716-e257-4ff9-9083-5a29649d0cc9
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
//        b40293f2-aec7-470f-92c7-34b9f6b15ebe

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
        UUID userId = UUID.fromString("16e8cb52-fa90-4a1d-bc00-016c18ee65c0");

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
