package nl.tudelft.sem.template.example.authorization;

import nl.tudelft.sem.template.example.service.AssociationService;
import org.mapstruct.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration class is necessary because AuthorizationFilter may not be annotated with @Component,
 * or the filter will be auto-injected for ALL requests! And we want to access the H2 console without that.
 */
@Configuration
public class AssociationFilterConfiguration {
    private transient AssociationService associationService;

    public AssociationFilterConfiguration(AssociationService associationService) {
        this.associationService = associationService;
    }

    /**
     * Enable registration.
     * @return the authorization filter
     */
    @Bean
    public FilterRegistrationBean<AssociationFilter> associationFilter() {
        AssociationFilter associationFilter = new AssociationFilter(associationService);

        FilterRegistrationBean<AssociationFilter> registration = new FilterRegistrationBean<>(associationFilter);
        registration.setEnabled(false);
        return registration;
    }
}
