package nl.tudelft.sem.template.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import nl.tudelft.sem.template.example.service.handlers.Handler;
import nl.tudelft.sem.template.example.service.handlers.RoleHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RoleHandlerTest {

    private transient HttpServletRequest request;
    private transient RoleHandler roleHandler;
    private transient ExternalService externalService;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        externalService = Mockito.mock(ExternalService.class);

        roleHandler = new RoleHandler(externalService);
    }

    @Test
    public void testAuthorizeCourierRoleWithUserId_ReturnsTrue() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("courier");
        when(externalService.verify("123", "courier")).thenReturn(true);
        boolean result = roleHandler.handle(request);

        assertTrue(result);
    }

    @Test
    public void testAuthorizeCourierRoleWithoutUserId_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getParameter("role")).thenReturn("courier");

        boolean result = roleHandler.handle(request);

        assertFalse(result);
    }

    @Test
    public void testAuthorize_VendorRoleWithoutUserId_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getParameter("role")).thenReturn("vendor");

        boolean result = roleHandler.handle(request);

        assertFalse(result);
    }

    @Test
    public void testAuthorizeNonCourierRole_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("non-courier");

        boolean result = roleHandler.handle(request);

        assertFalse(result);
    }

    @Test
    public void authorizeVendor() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("vendor");
        when(externalService.verify("123", "vendor")).thenReturn(true);
        boolean result = roleHandler.handle(request);

        assertTrue(result);
    }

    @Test
    public void authorizeAdmin() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("admin");
        when(externalService.verify("123", "admin")).thenReturn(true);
        boolean result = roleHandler.handle(request);

        assertTrue(result);
    }

    @Test
    public void authorizeCustomer() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("customer");
        when(externalService.verify("123", "customer")).thenReturn(true);
        boolean result = roleHandler.handle(request);

        assertTrue(result);
    }

    @Test
    public void testAuthorize_NullRequest_ReturnsFalse() {
        boolean result = roleHandler.handle(null);
        assertFalse(result);
    }

    @Test
    void testAllValidExceptExternalServiceVerify() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("courier");
        when(externalService.verify(any(), any())).thenReturn(false);

        boolean result = roleHandler.handle(request);

        assertFalse(result);
    }

    @Test
    public void testAuthorize_NullRole_ReturnsFalse() {
        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn(null);
        boolean result = roleHandler.handle(request);
        assertFalse(result);
    }

    @Test
    void testSetNext() {
        Handler handler = Mockito.mock(Handler.class);
        roleHandler.setNext(handler);
        assertEquals(roleHandler.next, handler);
    }

    @Test
    void checkNextTest() {
        Handler testHandler = Mockito.mock(Handler.class);

        when(testHandler.handle(request)).thenReturn(true);

        roleHandler.setNext(testHandler);
        assertTrue(roleHandler.checkNext(request));
    }

    @Test
    void checkNextTestFalse() {
        Handler testHandler = Mockito.mock(Handler.class);

        when(request.getHeader("X-User-Id")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("courier");
        when(testHandler.handle(request)).thenReturn(false);

        roleHandler.setNext(testHandler);
        assertFalse(roleHandler.checkNext(request));
    }

    @Test
    void checkNextNullTest() {
        roleHandler.setNext(null);
        assertTrue(roleHandler.checkNext(request));
    }
}