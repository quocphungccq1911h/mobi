package mobi.api.repository;

import mobi.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface Repository để tương tác với entity Category trong cơ sở dữ liệu.
 * Kế thừa JpaRepository để có các phương thức CRUD cơ bản.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Spring Data JPA sẽ tự động cung cấp các phương thức như save(), findById(), findAll(), deleteById(), v.v.

    // Định nghĩa các phương thức tìm kiếm tùy chỉnh nếu cần, ví dụ:
    Optional<Category> findByName(String name);

    Boolean existsByName(String name);
}
