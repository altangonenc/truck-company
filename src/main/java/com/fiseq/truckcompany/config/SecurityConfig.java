package com.fiseq.truckcompany.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/register").permitAll()
                .anyRequest().permitAll();
        http.csrf().disable(); // CSRF korumasını devre dışı bırak
        http.headers().frameOptions().disable(); // H2 veritabanı konsoluna erişim için X-Frame-Options'ı devre dışı bırak
        http.httpBasic().disable();
    }

}
