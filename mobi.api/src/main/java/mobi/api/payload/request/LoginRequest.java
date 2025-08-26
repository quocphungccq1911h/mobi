package mobi.api.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO cho yêu cầu đăng nhập.
 */
public class LoginRequest {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
