package com.fiseq.truckcompany.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(SpringDataRestConfiguration.class)
public class SpringFoxConfig {

    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("User API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fiseq.truckcompany.controller")) // UserController
                .paths(PathSelectors.ant("/users/**"))
                .build();
    }

    @Bean
    public Docket gameApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Game API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fiseq.truckcompany.controller")) // GameController
                .paths(PathSelectors.ant("/api/v1/game/**"))
                .build();
    }
}

