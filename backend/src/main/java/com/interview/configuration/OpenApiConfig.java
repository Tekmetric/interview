package com.interview.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${auth0.audience}")
    private String audience;

    @Bean
    public OpenAPI tekmetricOpenAPI() {
        var info = new Info()
                .title("Repair Shop Management API")
                .version("1.0")
                .description("API for managing repair services in an automotive repair shop");

        var components = new Components()
                .addSecuritySchemes("oauth2", new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("OAuth2 authentication with Auth0")
                        .flows(new OAuthFlows()
                                .implicit(new OAuthFlow()
                                        .authorizationUrl(issuerUri + "authorize?audience=" + audience)
                                        .tokenUrl(issuerUri + "oauth/token")
                                )
                        )
                );

        var securityRequirement = new SecurityRequirement()
                .addList("oauth2");

        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
