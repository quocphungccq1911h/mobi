package mobi.model;

import mobi.common.utils.CommonUtils; // Import từ module mobi.common

/**
 * Lớp User đại diện cho một người dùng trong hệ thống.
 */
public class User {
    private String id;
    private String name;
    private String email;

    // Constructor
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        // Sử dụng một phương thức từ mobi.common để minh họa dependency
        return CommonUtils.sayHello(name) + "\nUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}