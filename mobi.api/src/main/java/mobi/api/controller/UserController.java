package mobi.api.controller;

import jakarta.validation.Valid;
import mobi.api.payload.request.PasswordResetRequest;
import mobi.api.payload.request.UserUpdateRequest;
import mobi.api.payload.response.MessageResponse;
import mobi.api.repository.PasswordResetTokenRepository;
import mobi.api.repository.RoleRepository;
import mobi.api.repository.UserRepository;
import mobi.api.security.services.UserDetailsImpl;
import mobi.api.service.EmailService;
import mobi.model.entity.auth.ERole;
import mobi.model.entity.auth.PasswordResetToken;
import mobi.model.entity.auth.Role;
import mobi.model.entity.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST Controller để quản lý các API liên quan đến người dùng.
 * Yêu cầu JWT để truy cập. Sử dụng @PreAuthorize để phân quyền.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    PasswordResetTokenRepository passwordResetToken;

    @Autowired
    EmailService emailService;

    @Value("${mobi.app.frontendUrl}")
    private String frontendUrl;

    /**
     * Lấy tất cả người dùng. Chỉ ADMIN mới có thể truy cập.
     * GET /api/users
     *
     * @return Danh sách tất cả người dùng.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Loại bỏ password trước khi trả data
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    /**
     * Lấy thông tin người dùng theo ID.
     * ADMIN có thể xem tất cả. Người dùng có thể xem thông tin của chính họ.
     * GET /api/users/{id}
     *
     * @param id            ID của người dùng.
     * @param userPrincipal Thông tin người dùng đã xác thực (từ JWT).
     * @return ResponseEntity chứa thông tin người dùng hoặc lỗi.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userPrincipal) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("User not found with id: " + id), HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    /**
     * Cập nhật thông tin người dùng.
     * ADMIN có thể cập nhật mọi người dùng. Người dùng có thể cập nhật thông tin của chính họ (trừ vai trò).
     * PUT /api/users/{id}
     *
     * @param id                ID của người dùng cần cập nhật.
     * @param userUpdateRequest DTO chứa thông tin cập nhật.
     * @param userPrincipal     Thông tin người dùng đã xác thực (từ JWT).
     * @return ResponseEntity chứa thông tin người dùng đã cập nhật hoặc lỗi.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest, @AuthenticationPrincipal UserDetailsImpl userPrincipal) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("User not found with id: " + id), HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        // Kiểm tra quyền: Nếu không phải ADMIN và không phải user của chính họ, hoặc đang cố gắng thay đổi vai trò
        if (!userPrincipal.getAuthorities().stream().allMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) && !user.getId().equals(userPrincipal.getId())) {
            return new ResponseEntity<>(new MessageResponse("Access Denied: You are not authorized to update this user."), HttpStatus.FORBIDDEN);
        }

        // Cập nhật username
        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().isEmpty() && !userUpdateRequest.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userUpdateRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
            }
            user.setUsername(userUpdateRequest.getUsername());
        }

        // Cập nhật email
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty() && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }
            user.setEmail(userUpdateRequest.getEmail());
        }

        // Cập nhật mật khẩu (nếu có)
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(userUpdateRequest.getPassword()));
        }

        // Cập nhật vai trò (chỉ ADMIN mới có thể thực hiện)
        if (userUpdateRequest.getRoles() != null && !userUpdateRequest.getRoles().isEmpty()) {
            if (userPrincipal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                Set<Role> roles = new HashSet<>();
                userUpdateRequest.getRoles().forEach(roleStr -> {
                    switch (roleStr) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role Admin is not found."));
                            roles.add(adminRole);
                            break;
                        case "user":
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role User is not found."));
                            roles.add(userRole);
                            break;
                        default:
                            // Bỏ qua các vai trò không hợp lệ hoặc không xác định
                    }
                });
                user.setRoles(roles);
            } else {
                return new ResponseEntity<>(new MessageResponse("Access Denied: Only ADMIN can change roles."), HttpStatus.FORBIDDEN);
            }
        }

        User updatedUser = userRepository.save(user);
        updatedUser.setPassword(null);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Xóa người dùng theo ID. Chỉ ADMIN mới có thể truy cập.
     * DELETE /api/users/{id}
     *
     * @param id ID của người dùng cần xóa.
     * @return ResponseEntity với thông báo thành công hoặc lỗi.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>(new MessageResponse("User not found with id: " + id), HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    /**
     * API cho phép người dùng thay đổi mật khẩu của chính họ.
     * POST /api/users/change-password
     *
     * @param currentPassword Mật khẩu hiện tại của người dùng.
     * @param newPassword     Mật khẩu mới.
     * @param userPrincipal   Thông tin người dùng đã xác thực.
     * @return ResponseEntity với thông báo thành công hoặc lỗi.
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestParam String currentPassword, @RequestParam String newPassword, @AuthenticationPrincipal UserDetailsImpl userPrincipal) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("User not found."), HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if (!encoder.matches(currentPassword, user.getPassword())) {
            return new ResponseEntity<>(new MessageResponse("Invalid current password."), HttpStatus.BAD_REQUEST);
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    /**
     * API gửi token khôi phục mật khẩu đến email người dùng.
     * POST /api/users/forgot-password?email=...
     *
     * @param email Email của người dùng.
     * @return ResponseEntity với thông báo thành công hoặc lỗi.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("If your email exists in our system, a password reset link has been sent."), HttpStatus.OK);
        }

        User user = userOptional.get();
        PasswordResetToken token = new PasswordResetToken(user);
        passwordResetToken.save(token);

        String resetLink = frontendUrl + "/reset-password?token=" + token.getToken();
        String emailContent = "Để khôi phục mật khẩu, vui lòng truy cập liên kết sau: " + resetLink;

        emailService.sendEmail(user.getEmail(), "Khôi phục mật khẩu", emailContent);
        return ResponseEntity.ok(new MessageResponse("If your email exists in our system, a password reset link has been sent."));
    }

    /**
     * API đặt lại mật khẩu bằng token.
     * POST /api/users/reset-password
     *
     * @param request DTO chứa token và mật khẩu mới.
     * @return ResponseEntity với thông báo thành công hoặc lỗi.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        Optional<PasswordResetToken> tokenOptional = passwordResetToken.findByToken(request.getToken());

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            return new ResponseEntity<>(new MessageResponse("Invalid or expired password reset token."), HttpStatus.BAD_REQUEST);
        }

        User user = tokenOptional.get().getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));

        passwordResetToken.delete(tokenOptional.get()); // Xóa token sau khi đã sử dụng
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
    }

}
