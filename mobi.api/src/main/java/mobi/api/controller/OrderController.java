package mobi.api.controller;

import jakarta.transaction.Transactional;
import mobi.api.payload.response.MessageResponse;
import mobi.api.repository.CartItemRepository;
import mobi.api.repository.OrderRepository;
import mobi.api.repository.UserRepository;
import mobi.api.security.services.UserDetailsImpl;
import mobi.model.entity.CartItem;
import mobi.model.entity.Order;
import mobi.model.entity.OrderItem;
import mobi.model.entity.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller để quản lý các API liên quan đến đơn hàng.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Bảo vệ tất cả các endpoint trong controller này
public class OrderController {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository, CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Lấy tất cả các đơn hàng của người dùng hiện tại.
     *
     * @return Danh sách các đơn hàng.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForUser() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderRepository.findByUser(currentUser);
        return ResponseEntity.ok(orders);
    }

    /**
     * Lấy chi tiết một đơn hàng theo ID.
     *
     * @param orderId ID của đơn hàng.
     * @return Chi tiết đơn hàng.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this order.");
        }

        return ResponseEntity.ok(order);
    }

    /**
     * Đặt một đơn hàng mới từ các mặt hàng trong giỏ hàng.
     *
     * @return Phản hồi xác nhận đơn hàng đã được đặt.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<MessageResponse> placeOrder() {
        User currentUser = getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Giỏ hàng của bạn đang trống!"));
        }

        // Tạo một đối tượng Order mới
        Order order = new Order();
        order.setUser(currentUser);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Chuyển đổi các mặt hàng trong giỏ hàng thành các mặt hàng trong đơn hàng
        for (CartItem cartItem : cartItems) {
            BigDecimal itemPrice = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemPrice);

            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProduct().getPrice());
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // Xóa tất cả các mặt hàng trong giỏ hàng sau khi đặt đơn thành công
        cartItemRepository.deleteAll(cartItems);

        return ResponseEntity.ok(new MessageResponse("Đơn hàng đã được đặt thành công! Tổng số tiền: " + totalAmount));
    }

    /**
     * Phương thức tiện ích để lấy người dùng hiện tại từ SecurityContextHolder.
     *
     * @return Đối tượng User của người dùng hiện tại.
     */
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
