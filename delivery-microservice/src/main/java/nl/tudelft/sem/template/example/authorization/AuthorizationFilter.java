package nl.tudelft.sem.template.example.authorization;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.sem.template.example.service.filters.AssociationHandler;
import nl.tudelft.sem.template.example.service.filters.BaseHandler;
import nl.tudelft.sem.template.example.service.filters.Handler;
import nl.tudelft.sem.template.example.service.filters.RoleHandler;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;


public class AuthorizationFilter extends GenericFilterBean {

    private transient Handler roleHandler;
    private transient Handler associationHandler;

    public AuthorizationFilter(RoleHandler roleHandler, AssociationHandler associationHandler) {
        this.roleHandler = roleHandler;
        this.associationHandler = associationHandler;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // Chain of Responsibility
        Handler handler = roleHandler;
        handler.setNext(associationHandler);

        boolean authentication = handler.handle((HttpServletRequest) request);

        if (!authentication) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String errorMessage = "Authorization failed!";
            httpResponse.getWriter().write(errorMessage);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
