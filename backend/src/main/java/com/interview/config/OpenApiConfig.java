package com.interview.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Vehicle Management API",
                description = """
                        For authentication use the token:
                        eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG5ueSIsImFkbWluIjp0cnVlLCJpYXQiOjE3NTgyNDI2MTl9.HwaKWHSxkySZgsZ4FPk_CAThhugtIevDyU34S6kJWGoFSMbGwotUHezo9UbnB2cvtd03i8mOkYI2-S0bEBXeNafLQKFxfRwz0B3p5PJiHO8RbgAEQ6a0nCoJj43EmD79PFkHrVR8wnmLL8VaNbDXlEt8V30PRv6BFj_wqTr4qFEa-DslRMCSLgW9avvVJ0rNcSufLwN6H-Czm8Gpl93Y7o8eNXC8exYFFdkgqO5FU-ve4Jki-nXvyaddNcoYv11Yr2r-4jDFI0H5QDEkt1YC0a_ZDfCQg2mTKQIXyx4Ucm49SnrXbgMxpShxUE_HuSFCdlVV7H8KCqVZgsxIOHpGNQ
                        """,
                version = "v1"
        )
)
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Authorization",
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
    // empty
}
