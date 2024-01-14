package nl.tudelft.sem.template.example.authorization;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.sem.template.example.service.AssociationService;
import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;


public class AssociationFilter extends GenericFilterBean {

    private final transient AssociationService associationService;

    public AssociationFilter(AssociationService associationService) {
        this.associationService = associationService;
    }

    /**
     * Filter to check the association of sensitive objects and the requesting user ID.
     *
     * @param request     The request to process
     * @param response    The response associated with the request
     * @param filterChain Provides access to the next filter in the chain for this
     *                    filter to pass the request and response to for further
     *                    processing
     * @throws IOException If an I/O error occurs during this filter's processing of
     * @throws ServletException If the processing fails for any other reason
     */
    @lombok.Generated
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        boolean authentication = associationService.authorize((HttpServletRequest) request);
        if (!authentication) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String errorMessage = "Authorization failed! The object you are requesting does not belong to you.";
            httpResponse.getWriter().write(errorMessage);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
