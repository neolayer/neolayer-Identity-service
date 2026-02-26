package com.neolayer.identity.config;

import com.neolayer.identity.entity.Role;
import com.neolayer.identity.entity.User;
import com.neolayer.identity.repository.RoleRepository;
import com.neolayer.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Seeds the database with a default system user on startup.
 * This ensures userId=1 always exists so OAuth clients can be created
 * without having to register first.
 *
 * Default credentials:
 * email: admin@neolayer.com
 * password: admin@123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // Ensure ROLE_USER exists
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name("ROLE_USER").description("Default user role").build()));

        // Ensure ROLE_ADMIN exists
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name("ROLE_ADMIN").description("Administrator role").build()));

        // Seed default system/admin user if not present
        if (!userRepository.existsByEmail("admin@neolayer.com")) {
            User admin = User.builder()
                    .email("admin@neolayer.com")
                    .password(passwordEncoder.encode("admin@123"))
                    .firstName("NeoLayer")
                    .lastName("Admin")
                    .username("neolayer_admin")
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .roles(Set.of(userRole, adminRole))
                    .build();

            userRepository.save(admin);
            log.info("✅ Default admin user seeded — email: admin@neolayer.com  password: admin@123");
        }
    }
}
