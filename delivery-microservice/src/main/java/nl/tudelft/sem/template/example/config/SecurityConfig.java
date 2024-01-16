package nl.tudelft.sem.template.example.config;

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
    private transient AssociationFilterConfiguration associationFilterConfiguration;

    public SecurityConfig(AuthorizationFilterConfiguration authorizationFilterConfiguration,
                          AssociationFilterConfiguration associationFilterConfiguration) {
        this.authorizationFilterConfiguration = authorizationFilterConfiguration;
        this.associationFilterConfiguration = associationFilterConfiguration;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.regexMatcher("^(?!.*/h2-console/).*")
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("**/**")
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authorizationFilterConfiguration.authorizationFilter().getFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(associationFilterConfiguration.associationFilter().getFilter(),
                        UsernamePasswordAuthenticationFilter.class);
    }
}
