package nl.tudelft.sem.template.example.authorization;

import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.mapstruct.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration class is necessary because AuthorizationFilter may not be annotated with @Component,
 * or the filter will be auto-injected for ALL requests! And we want to access the H2 console without that.
 */
@Configuration
public class AuthorizationFilterConfiguration {
    private transient AuthorizationService authorizationService;

    public AuthorizationFilterConfiguration(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Enable registration.
     * @return the authorization filter
     */
    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(authorizationService);

        FilterRegistrationBean<AuthorizationFilter> registration = new FilterRegistrationBean<>(authorizationFilter);
        registration.setEnabled(false);

        return registration;
    }
}
