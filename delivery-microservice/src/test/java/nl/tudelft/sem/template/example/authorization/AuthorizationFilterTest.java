package nl.tudelft.sem.template.example.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.example.service.filters.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;

class AuthorizationFilterTest {

    private transient AuthorizationFilter authorizationFilter;
    private transient AuthorizationService authorizationService;
    private transient HttpServletRequest request;
    private transient FilterChain filterChain;

    @BeforeEach
    void setUp() {
        authorizationService = Mockito.mock(AuthorizationService.class);
        authorizationFilter = new AuthorizationFilter(authorizationService);
        request = Mockito.mock(HttpServletRequest.class);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    public void testDoFilter_AuthorizationSuccess() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authorizationService.authorize(Mockito.any())).thenReturn(true);

        authorizationFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDoFilter_AuthorizationFailure() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authorizationService.authorize(request)).thenReturn(false);

        authorizationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Authorization failed!", response.getContentAsString());
    }
}