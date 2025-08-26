package mobi.api.payload.response;

/**
 * DTO cho các thông báo phản hồi đơn giản (ví dụ: thông báo thành công/thất bại).
 */
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter and Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
