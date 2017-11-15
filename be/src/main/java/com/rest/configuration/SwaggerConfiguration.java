package com.rest.configuration;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.rest.controller"))
                .paths(paths())
                .build();
    }

    private Predicate<String> paths() {
        return or(
                regex("/api/.*"),
                regex("/sys/.*")
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("DashboardIO API")
                .description("DashboardIO API reference for developers")
                .contact("Nizami Islamovs : nizamiislamovs@gmail.com")
                .version("1.0")
                .build();
    }
}
