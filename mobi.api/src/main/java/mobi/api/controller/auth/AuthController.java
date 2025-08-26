package mobi.api.controller.auth;

import jakarta.validation.Valid;
import mobi.api.payload.request.LoginRequest;
import mobi.api.payload.request.SignupRequest;
import mobi.api.payload.response.JwtResponse;
import mobi.api.payload.response.MessageResponse;
import mobi.api.repository.RoleRepository;
import mobi.api.repository.UserRepository;
import mobi.api.security.jwt.JwtUtils;
import mobi.api.security.services.UserDetailsImpl;
import mobi.model.entity.auth.ERole;
import mobi.model.entity.auth.Role;
import mobi.model.entity.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller để xử lý các yêu cầu liên quan đến xác thực (đăng ký, đăng nhập).
 */
@CrossOrigin(origins = "*", maxAge = 3600) // Cho phép yêu cầu từ bất kỳ origin nào (trong phát triển)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Endpoint để đăng nhập người dùng và nhận JWT.
     * POST /api/auth/signin
     *
     * @param loginRequest DTO chứa username và password.
     * @return ResponseEntity chứa JWT và thông tin người dùng.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Xác thực người dùng bằng username và password
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
        // Thiết lập ngữ cảnh bảo mật
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication); // Tạo jwt

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        // Trả về JWT và thông tin người dùng
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    /**
     * Endpoint để đăng ký người dùng mới.
     * POST /api/auth/signup
     *
     * @param signUpRequest DTO chứa username, email, password và các vai trò.
     * @return ResponseEntity chứa thông báo thành công hoặc lỗi.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Tạo User mới (mã hóa mật khẩu)
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        // Gán vai trò cho người dùng
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
