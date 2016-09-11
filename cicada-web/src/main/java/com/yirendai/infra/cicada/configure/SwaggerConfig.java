package com.yirendai.infra.cicada.configure;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
  @Bean
  public Docket documentation() {
    return new Docket(DocumentationType.SWAGGER_2) //
        .select() //
        .apis(RequestHandlerSelectors.any()) //
        .paths(regex("/.*"))
        .build().apiInfo(metadata());
  }

  @Bean
  public UiConfiguration uiConfig() {
    return UiConfiguration.DEFAULT;
  }

  private ApiInfo metadata() {
    return new ApiInfoBuilder().title("cicada-web API") //
        .description("cicada-web API") //
        .version("1.0").build();
  }
}
