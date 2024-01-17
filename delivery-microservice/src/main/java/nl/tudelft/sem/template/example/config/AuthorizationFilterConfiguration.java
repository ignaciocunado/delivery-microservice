package nl.tudelft.sem.template.example.config;

import nl.tudelft.sem.template.example.authorization.AuthorizationFilter;
import nl.tudelft.sem.template.example.service.filters.RoleHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration class is necessary because AuthorizationFilter may not be annotated with @Component,
 * or the filter will be auto-injected for ALL requests! And we want to access the H2 console without that.
 */
@Configuration
public class AuthorizationFilterConfiguration {
    private transient RoleHandler roleHandler;

    public AuthorizationFilterConfiguration(RoleHandler roleHandler) {
        this.roleHandler = roleHandler;
    }

    /**
     * Enable registration.
     * @return the authorization filter
     */
    @lombok.Generated
    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
        AuthorizationFilter authorizationFilter = new AuthorizationFilter(roleHandler);

        FilterRegistrationBean<AuthorizationFilter> registration = new FilterRegistrationBean<>(authorizationFilter);
        registration.setEnabled(false);

        return registration;
    }
}
