package mobi.api.security.config;

import mobi.api.security.jwt.AuthEntryPointJwt;
import mobi.api.security.jwt.AuthTokenFilter;
import mobi.api.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Lớp cấu hình chính cho Spring Security trong ứng dụng.
 */
@Configuration
@EnableMethodSecurity // Kích hoạt bảo mật dựa trên phương thức (@PreAuthorize, @PostAuthorize)
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Bean để tạo AuthTokenFilter
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Bean để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean để cung cấp cơ chế xác thực dựa trên UserDetailsService và PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // Bean để quản lý quá trình xác thực
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Cấu hình chuỗi lọc bảo mật
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Tắt CSRF (thường làm với stateless APIs và JWT)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Xử lý lỗi xác thực
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không sử dụng session (stateless)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập API đăng ký/đăng nhập
                                .requestMatchers("/api/test/**").permitAll() // Cho phép truy cập các API test
                                .requestMatchers("/api/products/**").permitAll() // Tạm thời cho phép truy cập products (sẽ bảo vệ sau)
                                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // CẬP NHẬT LẦN NỮA: Cho phép tất cả các đường dẫn liên quan đến Swagger
                                .anyRequest().authenticated() // Tất cả các yêu cầu khác đều cần xác thực
                );

        http.authenticationProvider(authenticationProvider()); // Đăng ký Authentication Provider

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Thêm JWT filter trước UsernamePasswordAuthenticationFilter

        return http.build();
    }
}
