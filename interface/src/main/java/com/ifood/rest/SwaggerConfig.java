package com.ifood.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build().apiInfo(metadateApi());
    }

    private ApiInfo metadateApi() {
        return new ApiInfo(
                "iFood-Connection",
                "Api to control the functionalities related to connection control with the restaurants",
                "0.0.1",
                "",
                new Contact("Eriky Raggeoto Kashivagui", "", "mushira4@gmail.com"),
                "",
                "",
                new ArrayList<>()
        );
    }
}
