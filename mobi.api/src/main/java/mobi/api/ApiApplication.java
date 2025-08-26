package mobi.api;

import mobi.common.utils.CommonUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lớp khởi chạy chính cho ứng dụng API, bây giờ là một ứng dụng Spring Boot.
 */

@SpringBootApplication
@EntityScan(basePackages = "mobi.model.entity")
@EnableJpaRepositories(basePackages = "mobi.api.repository")
@RestController
@EnableCaching
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        System.out.println("Ứng dụng mobi.api đã khởi động với Spring Boot, MySQL và Redis!");
    }
}