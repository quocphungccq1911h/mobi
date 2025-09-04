package mobi.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import mobi.api.payload.request.CartItemRequest;
import mobi.api.payload.response.MessageResponse;
import mobi.api.repository.CartItemRepository;
import mobi.api.repository.ProductRepository;
import mobi.api.repository.UserRepository;
import mobi.api.security.services.UserDetailsImpl;
import mobi.model.entity.CartItem;
import mobi.model.entity.Product;
import mobi.model.entity.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/cart")
@RestController
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Bảo vệ tất cả các endpoint trong controller này
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Lấy tất cả các mặt hàng trong giỏ hàng của người dùng hiện tại.
     *
     * @return Danh sách các mặt hàng trong giỏ hàng.
     */
    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems() {
        User currentUser = getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);
        return ResponseEntity.ok(cartItems);
    }

    /**
     * Thêm một sản phẩm vào giỏ hàng hoặc cập nhật số lượng nếu đã tồn tại.
     *
     * @param cartItemRequest Dữ liệu sản phẩm và số lượng.
     * @return Phản hồi xác nhận.
     */
    @PostMapping()
    @Transactional
    public ResponseEntity<MessageResponse> addProductToCart(@Valid @RequestBody CartItemRequest cartItemRequest) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndProduct(currentUser, product);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.getQuantity());
            cartItemRepository.save(cartItem);
            return ResponseEntity.ok(new MessageResponse("Product quantity updated in cart successfully!"));
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(currentUser);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(cartItemRequest.getQuantity());
            cartItemRepository.save(newCartItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Product added to cart successfully!"));
        }
    }

    /**
     * Cập nhật số lượng của một mặt hàng trong giỏ hàng.
     *
     * @param productId       ID của sản phẩm cần cập nhật.
     * @param cartItemRequest Dữ liệu số lượng mới.
     * @return Phản hồi xác nhận.
     */
    @PutMapping("/update/{productId}")
    @Transactional
    public ResponseEntity<MessageResponse> updateCartItem(@PathVariable Long productId, @Valid @RequestBody CartItemRequest cartItemRequest) {
        User currentUser = getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(currentUser, product)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItemRepository.save(cartItem);

        return ResponseEntity.ok(new MessageResponse("Cart item quantity updated successfully!"));
    }

    /**
     * Xóa một mặt hàng khỏi giỏ hàng.
     *
     * @param productId ID của sản phẩm cần xóa.
     * @return Phản hồi xác nhận.
     */
    @DeleteMapping("/remove/{productId}")
    @Transactional
    public ResponseEntity<MessageResponse> removeCartItem(@PathVariable Long productId) {
        User currentUser = getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        cartItemRepository.deleteByUserAndProduct(currentUser, product);

        return ResponseEntity.ok(new MessageResponse("Product removed from cart successfully!"));
    }


    /**
     * Phương thức tiện ích để lấy người dùng hiện tại từ SecurityContextHolder.
     *
     * @return Đối tượng User của người dùng hiện tại.
     */
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    }
}
