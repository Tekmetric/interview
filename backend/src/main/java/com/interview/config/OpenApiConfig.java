package com.interview.config;

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

    private static final String SECURITY_PROVIDER_NAME = "keycloak";

    @Value("${com.c4-soft.springaddons.oidc.ops[0].iss}")
    private String keycloakIssuer;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vehicle Management API")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_PROVIDER_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_PROVIDER_NAME, createOAuthScheme()));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow flow = new OAuthFlow();

        flow.setAuthorizationUrl(keycloakIssuer + "/protocol/openid-connect/auth");
        flow.setTokenUrl(keycloakIssuer + "/protocol/openid-connect/token");
        flow.setRefreshUrl(keycloakIssuer + "/protocol/openid-connect/token");

        flows.setAuthorizationCode(flow);

        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Keycloak OAuth2 with user login")
                .flows(flows);
    }
}
