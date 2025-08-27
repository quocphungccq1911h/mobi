package mobi.api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mobi.api.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bộ lọc (filter) để xử lý JWT từ các yêu cầu HTTP.
 */
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("AuthTokenFilter: Intercepting request for " + request.getRequestURI()); // Dòng log để debug
        try {
            // Logic chính để xử lý JWT đã được THÊM LẠI
            String jwt = parseJwt(request);  // Lấy JWT từ Header Authorization
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {// Xác thực jwt
                String userName = jwtUtils.getUserNameFromJwtToken(jwt);// Lấy username từ JWT

                UserDetails userDetails = userDetailsService.loadUserByUsername(userName); // Tải thông tin người dùng
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // Thiết lập ngữ cảnh bảo mật

                // DÒNG QUAN TRỌNG NHẤT BỊ THIẾU TRONG LẦN CẬP NHẬT TRƯỚC
                SecurityContextHolder.getContext().setAuthentication(authentication); // Thiết lập ngữ cảnh bảo mật
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response); // Chuyển yêu cầu đến bộ lọc tiếp theo
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Trích xuất token
        }
        return null;
    }
}
