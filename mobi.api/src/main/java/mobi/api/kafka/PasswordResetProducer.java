package mobi.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Lớp Kafka Producer để gửi message đặt lại mật khẩu.
 */
@Component
public class PasswordResetProducer {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetProducer.class);
    private static final String TOPIC = "password-reset-topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Gửi một đối tượng message tới topic Kafka.
     *
     * @param message Đối tượng message cần gửi (ví dụ: một đối tượng JSON).
     */
    public void sendMessage(Object message) {
        logger.info(String.format("Gửi message tới topic %s: %s", TOPIC, message.toString()));
        this.kafkaTemplate.send(TOPIC, message);
    }
}
