package nl.tudelft.sem.template.example.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthorizationServiceTest {

    private transient HttpServletRequest request;
    private transient AuthorizationService authorizationService;
    @Autowired
    private transient ExternalService externalService;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);

        authorizationService = new AuthorizationService(externalService);
    }

    @Test
    public void testAuthorizeCourierRoleWithUserId_ReturnsTrue() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("courier");

        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    public void testAuthorizeCourierRoleWithoutUserId_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getParameter("role")).thenReturn("courier");

        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    public void testAuthorize_VendorRoleWithoutUserId_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getParameter("role")).thenReturn("vendor");

        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    public void testAuthorizeNonCourierRole_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("non-courier");

        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    public void authorizeVendor() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("vendor");

        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    public void authorizeAdmin() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("admin");

        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    public void authorizeCustomer() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("customer");

        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    public void testAuthorize_NullRequest_ReturnsFalse() {
        boolean result = authorizationService.authorize(null);
        assertFalse(result);
    }

    @Test
    public void testAuthorize_NullRole_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn(null);
        boolean result = authorizationService.authorize(request);
        assertFalse(result);
    }
}