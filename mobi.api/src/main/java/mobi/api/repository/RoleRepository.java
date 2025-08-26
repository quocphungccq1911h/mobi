package mobi.api.repository;

import mobi.model.entity.auth.ERole;
import mobi.model.entity.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface Repository để tương tác với entity Role trong cơ sở dữ liệu.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole roleName);
}
