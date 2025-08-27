package mobi.api.service;

/**
 * Interface cho dịch vụ gửi email.
 */
public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
