package br.com.ifood.ifoodbackendconnection.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;

@Configuration
@EnableSwagger2
//@Import(springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class)
public class SwaggerConfiguration {

    @Bean
    public Docket apiVersion1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("v1")
                .apiInfo(metadata())
                .useDefaultResponseMessages(false)
                .select()
                .apis(withClassAnnotation(RestController.class))
                .paths(regex("/api/v1/.*"))
                .build();
    }


    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .validatorUrl(null)
                .docExpansion(DocExpansion.NONE)
                .operationsSorter(OperationsSorter.ALPHA)
                .supportedSubmitMethods(new String[] { "get", "post", "put", "delete" })
                .displayRequestDuration(true)
                .build();
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Swagger Ifood Backend Connection")
                .description("Ifood Backend Connection Service")
                .version("1.0.0")
                .build();
    }
}
