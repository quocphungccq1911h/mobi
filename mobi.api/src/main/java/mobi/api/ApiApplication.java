package mobi.api;

import mobi.common.utils.CommonUtils;
import mobi.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lớp khởi chạy chính cho ứng dụng API, bây giờ là một ứng dụng Spring Boot.
 */

@SpringBootApplication
@RestController
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        System.out.println("Ứng dụng mobi.api đã khởi động với Spring Boot!");
    }

    // Một endpoint ví dụ để kiểm tra
    @GetMapping("/")
    public String home() {
        return "Xin chào từ Spring Boot API! Sử dụng CommonUtils: " + CommonUtils.sayHello("User") + ".";
    }

    @GetMapping("/user-example")
    public User userExample() {
        return new User("002", "Jane Doe", "jane.doe@example.com");
    }
}