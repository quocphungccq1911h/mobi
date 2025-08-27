package mobi.api.payload.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO cho yêu cầu cập nhật thông tin người dùng.
 * Các trường là tùy chọn (nullable) nếu không muốn cập nhật.
 */
public class UserUpdateRequest {
    @Size(min = 3, max = 20)
    private String username; // Tên người dùng có thể thay đổi, nhưng giữ ràng buộc độ dài

    @Size(max = 50)
    @Email
    private String email; // Email có thể thay đổi

    @Size(min = 6, max = 40)
    private String password; // Mật khẩu có thể thay đổi

    private Set<String> roles; // Vai trò có thể thay đổi (chỉ ADMIN mới có thể cập nhật)

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
