package mobi.api.repository;

import mobi.model.entity.CartItem;
import mobi.model.entity.Product;
import mobi.model.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface Repository để tương tác với entity CartItem trong cơ sở dữ liệu.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Tìm kiếm một mặt hàng trong giỏ hàng của một người dùng dựa trên sản phẩm.
     *
     * @param user    Người dùng sở hữu giỏ hàng.
     * @param product Sản phẩm cần tìm.
     * @return Một Optional chứa CartItem nếu tìm thấy.
     */
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    /**
     * Tìm kiếm tất cả các mặt hàng trong giỏ hàng của một người dùng.
     *
     * @param user Người dùng sở hữu giỏ hàng.
     * @return Danh sách các CartItem.
     */
    List<CartItem> findByUser(User user);

    /**
     * Xóa một mặt hàng cụ thể trong giỏ hàng của người dùng.
     *
     * @param user    Người dùng sở hữu giỏ hàng.
     * @param product Sản phẩm cần xóa.
     */
    void deleteByUserAndProduct(User user, Product product);
}
