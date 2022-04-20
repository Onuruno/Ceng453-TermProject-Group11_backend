package com.group11.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Controller
@SpringBootApplication
public class ServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);

    }

    /**
     * This method configures Swagger by creating a Docket API instance
     *
     * @return New prepared instance of Docket for Swagger configuration
     */
    @Bean
    public Docket swaggerConfiguration() {
        // Returns prepared Docket instance
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/api/*"))
                .apis(RequestHandlerSelectors.basePackage("com.group11"))
                .build()
                .apiInfo(apiDetails());
    }

    /**
     * This method creates API details for APi info part of prepared Docket instance.
     *
     * @return New API information instance filled by ApiInfoBuilder
     */
    private ApiInfo apiDetails() {
        return new ApiInfoBuilder()
                .title("Game Server for CENG 453 Term Project")
                .description("Sample API using Spring Boot, MariaDB and Swagger")
                .license("Free To Use")
                .version("0.1")
                .build();
    }
}
