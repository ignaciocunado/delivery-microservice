package nl.tudelft.sem.template.example.authentication;

import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        boolean authentication = AuthorizationService.authorize((HttpServletRequest) request);
        if(!authentication){
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String errorMessage = "Authorization failed";
            httpResponse.getWriter().write(errorMessage);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
