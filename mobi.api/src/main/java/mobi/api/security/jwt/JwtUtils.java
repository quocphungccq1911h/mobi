package mobi.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import mobi.api.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Lớp tiện ích để tạo (generate) và xác thực (validate) JSON Web Tokens (JWT).
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}") // Lấy giá trị từ application.properties
    private String jwtSecret;

    @Value("${jwt.expiration}") // Lấy giá trị từ application.properties
    private int jwtExpirationMs;

    /**
     * Tạo JWT từ thông tin xác thực của người dùng.
     *
     * @param authentication Đối tượng Authentication chứa thông tin người dùng đã xác thực.
     * @return Chuỗi JWT đã tạo.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Đặt chủ thể của token là username
                .setIssuedAt(new Date()) // Thời gian tạo token
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256) // Ký token với thuật toán HS256 và secret key
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Lấy username từ JWT.
     *
     * @param token Chuỗi JWT.
     * @return Username.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Xác thực JWT.
     *
     * @param authToken Chuỗi JWT cần xác thực.
     * @return true nếu token hợp lệ, ngược lại false.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Sử dụng parserBuilder() thay cho parser()
            Jwts.parser().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
