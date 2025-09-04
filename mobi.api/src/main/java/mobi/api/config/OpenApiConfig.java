package mobi.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình OpenAPI (Swagger).
 * Thêm header Authorization cho Swagger UI để gửi JWT token.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mobi API Documentation",
                version = "1.0",
                description = "API documentation for the Mobi e-commerce application."
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT authentication using a bearer token. <br> **Enter your token in the format 'Bearer &lt;token&gt;'.**"
)
public class OpenApiConfig {
    // Lớp này không cần chứa bất kỳ phương thức nào.
    // Các annotation trên lớp này đã đủ để cấu hình Swagger.
}
