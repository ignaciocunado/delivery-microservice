package nl.tudelft.sem.template.example.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import nl.tudelft.sem.template.example.service.filters.AssociationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;

class AssociationFilterTest {

    private transient AssociationFilter associationFilter;
    private transient AssociationHandler associationHandler;
    private transient HttpServletRequest request;
    private transient FilterChain filterChain;

    @BeforeEach
    void setUp() {
        associationHandler = Mockito.mock(AssociationHandler.class);
        associationFilter = new AssociationFilter(associationHandler);
        request = Mockito.mock(HttpServletRequest.class);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    public void testDoFilter_AuthorizationSuccess() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(associationHandler.handle(Mockito.any())).thenReturn(true);

        associationFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDoFilter_AuthorizationFailure() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(associationHandler.handle(request)).thenReturn(false);

        associationFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Authorization failed! The object you are requesting does not belong to you.",
                response.getContentAsString());
    }
}