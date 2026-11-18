package br.com.fiap.sampasense.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Habilita o botão "Authorize" do Swagger para enviar o token JWT nas requisições. */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "SampaSense API", version = "1.0",
                description = "Alerta de alagamento e rota segura em tempo real para vales e túneis de São Paulo. "
                        + "Faça login em POST /api/auth/login e clique em Authorize para usar as rotas protegidas."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
