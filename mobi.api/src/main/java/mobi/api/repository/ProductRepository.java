package mobi.api.repository;

import mobi.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring Data JPA sẽ tự động cung cấp các phương thức như save(), findById(), findAll(), deleteById(), v.v.
    // Bạn có thể định nghĩa các phương thức tìm kiếm tùy chỉnh tại đây nếu cần, ví dụ:
    // List<Product> findByNameContainingIgnoreCase(String name);
}
