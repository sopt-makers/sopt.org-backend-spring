package sopt.org.homepage.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sopt.org.homepage.global.common.constants.SecurityConstants;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info().title("SOPT 공식홈페이지")
                .description("Spring V2 API 문서");

        String jwtSchemeName = SecurityConstants.SCHEME_NAME;

        Components components = new Components().addSecuritySchemes(jwtSchemeName,
                new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(jwtSchemeName)

        );

        // To Add JWT Authorization Header, add this annotation to API
        // @SecurityRequirement(name = SecurityConstants.SCHEME_NAME)

        List<Server> serverV2 = List.of(new Server().url("/v2"));

        return new OpenAPI().info(info).components(components).servers(serverV2);
    }
}
