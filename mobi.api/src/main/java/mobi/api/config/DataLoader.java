package mobi.api.config;

import mobi.api.repository.RoleRepository;
import mobi.model.entity.auth.ERole;
import mobi.model.entity.auth.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra và tạo ROLE_ADMIN nếu chưa tồn tại
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            System.out.println("ROLE_ADMIN created.");
        }
        // Kiểm tra và tạo ROLE_USER nếu chưa tồn tại
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            System.out.println("ROLE_USER created.");
        }
    }
}
