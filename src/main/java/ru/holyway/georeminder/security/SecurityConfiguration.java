package ru.holyway.georeminder.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * Created by seiv0814 on 07-11-17.
 */
@Configuration
public class SecurityConfiguration extends
        WebSecurityConfigurerAdapter {

    private final AnonymousAuthenticationFilter anonymousAuthenticationFilter;

    public SecurityConfiguration(final AnonymousAuthenticationFilter anonymousAuthenticationFilter) {

        this.anonymousAuthenticationFilter = anonymousAuthenticationFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**"); // #3
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.anonymous().authenticationFilter(anonymousAuthenticationFilter);
    }
}
