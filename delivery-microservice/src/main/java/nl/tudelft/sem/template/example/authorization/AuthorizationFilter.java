package nl.tudelft.sem.template.example.authorization;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.hibernate.service.spi.InjectService;
import org.mapstruct.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;


public class AuthorizationFilter extends GenericFilterBean {

    private transient AuthorizationService authorizationService;

    public AuthorizationFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        boolean authentication = authorizationService.authorize((HttpServletRequest) request);
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
