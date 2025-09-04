package mobi.api.repository;

import mobi.model.entity.Order;
import mobi.model.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Tìm kiếm tất cả các đơn hàng của một người dùng cụ thể.
     *
     * @param user Người dùng sở hữu đơn hàng.
     * @return Danh sách các đơn hàng.
     */
    List<Order> findByUser(User user);
}
