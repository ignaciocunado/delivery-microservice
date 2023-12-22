package nl.tudelft.sem.template.example.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AuthorizationServiceTest {

    private transient HttpServletRequest request;
    private transient AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        authorizationService = new AuthorizationService();
    }

    @Test
    public void testAuthorize_CourierRoleWithUserId_ReturnsTrue() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("courier");

        boolean result = authorizationService.authorize(request);

        assertTrue(result);
    }

    @Test
    public void testAuthorize_CourierRoleWithoutUserId_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getParameter("role")).thenReturn("courier");

        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }

    @Test
    public void testAuthorize_NonCourierRole_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("non-courier");

        boolean result = authorizationService.authorize(request);

        assertFalse(result);
    }
}