package nl.tudelft.sem.template.example.config;

import nl.tudelft.sem.template.example.authorization.AuthorizationFilter;
import nl.tudelft.sem.template.example.authorization.AuthorizationFilterConfiguration;
import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.hibernate.Incubating;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private transient AuthorizationFilterConfiguration authorizationFilterConfiguration;

    public SecurityConfig(AuthorizationFilterConfiguration authorizationFilterConfiguration) {
        this.authorizationFilterConfiguration = authorizationFilterConfiguration;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.regexMatcher("^(?!.*/h2-console/).*")
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(authorizationFilterConfiguration.authorizationFilter().getFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }

}
