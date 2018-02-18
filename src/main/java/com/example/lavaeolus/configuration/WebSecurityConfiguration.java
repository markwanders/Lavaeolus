package com.example.lavaeolus.configuration;

import com.example.lavaeolus.security.AuthenticationFilter;
import com.example.lavaeolus.security.LoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling().and()
                .anonymous().and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/service-worker.js").permitAll()
                .antMatchers("/bower_components/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/src/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoginFilter("/login"), UsernamePasswordAuthenticationFilter.class);
    }
}
