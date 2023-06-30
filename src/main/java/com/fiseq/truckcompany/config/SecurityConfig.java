package com.fiseq.truckcompany.config;

import com.fiseq.truckcompany.service.UserServiceImpl;
import com.fiseq.truckcompany.utilities.JwtAuthenticationFilter;
import com.fiseq.truckcompany.utilities.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl userService;

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/register").permitAll()
                .anyRequest().permitAll();
        http.csrf().disable(); // CSRF korumasını devre dışı bırak
        http.headers().frameOptions().disable(); // H2 veritabanı konsoluna erişim için X-Frame-Options'ı devre dışı bırak
        http.httpBasic().disable();
    }*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/users/register").permitAll() // Kayıt işlemine herkesin erişimi olmalı
                .antMatchers("/users/recovery-question").permitAll()
                .antMatchers("/users/change-password").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
