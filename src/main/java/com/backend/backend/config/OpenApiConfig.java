package com.backend.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "biniyam nahusenay",
                        email = "bininahu12@gmail.com.com",
                        url = "https://local.com"
                ),
                description = "OpenAPI documentation for Mobile Banking Application",
                title = "Mobile Banking API",
                version = "1.0",
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(description = "Production", url = "http://bankapp-production-874c.up.railway.app"),
                @Server(description = "Local", url = "http://localhost:8080")
        },
        security = { @SecurityRequirement(name = "bearerAuth") }
)

@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}