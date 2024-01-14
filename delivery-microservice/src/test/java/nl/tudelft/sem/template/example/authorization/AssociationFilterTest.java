package nl.tudelft.sem.template.example.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import nl.tudelft.sem.template.example.service.AssociationService;
import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;

class AssociationFilterTest {

    private transient AssociationFilter associationFilter;
    private transient AssociationService associationService;
    private transient HttpServletRequest request;
    private transient FilterChain filterChain;

    @BeforeEach
    void setUp() {
        associationService = Mockito.mock(AssociationService.class);
        associationFilter = new AssociationFilter(associationService);
        request = Mockito.mock(HttpServletRequest.class);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    public void testDoFilter_AuthorizationSuccess() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(associationService.authorize(Mockito.any())).thenReturn(true);

        associationFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDoFilter_AuthorizationFailure() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(associationService.authorize(request)).thenReturn(false);

        associationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Authorization failed! The object you are requesting does not belong to you.",
                response.getContentAsString());
    }
}