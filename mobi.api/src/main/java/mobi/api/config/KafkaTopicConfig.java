package mobi.api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Lớp cấu hình Kafka để tạo topic tự động khi ứng dụng khởi động.
 */
@Configuration
public class KafkaTopicConfig {
    /**
     * Tạo một bean NewTopic để đăng ký một topic mới trên Kafka.
     *
     * @return NewTopic
     */
    @Bean
    public NewTopic passwordResetTopic() {
        return TopicBuilder.name("password-reset-topic")
                .partitions(1) // Số lượng phân vùng
                .replicas(1) // Số lượng bản sao
                .build();
    }
}
