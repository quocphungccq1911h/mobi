package mobi.api;

import mobi.common.utils.CommonUtils;
import mobi.model.User;

/**
 * Lớp khởi chạy chính cho ứng dụng API.
 * (Sau này sẽ tích hợp Spring Boot vào đây)
 */
public class ApiApplication {
    public static void main(String[] args) {
        System.out.println("Khởi động ứng dụng mobi.api...");

        // Minh họa việc sử dụng các lớp từ các module khác
        String greeting = CommonUtils.sayHello("Thế giới");
        System.out.println(greeting);

        User newUser = new User("001", "Alice", "alice@example.com");
        System.out.println(newUser);

        System.out.println("Ứng dụng mobi.api đã sẵn sàng!");
    }
}