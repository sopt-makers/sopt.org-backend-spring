package sopt.org.homepage.config;


import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI openAPI() {

                Info info = new Info().title("SOPT 공식홈페이지")
                        .description("Spring V2 API 문서");

                String jwtSchemeName = "Authorization";
                SecurityRequirement securityRequirement =
                        new SecurityRequirement().addList(jwtSchemeName);

                Components components = new Components().addSecuritySchemes(jwtSchemeName,
                        new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name(jwtSchemeName)
                );

                List<Server> serverV2 = List.of(new Server().url("/v2"));

                return new OpenAPI().info(info).addSecurityItem(securityRequirement)
                        .components(components).servers(serverV2);
        }
}
