package org.bloomberg.keezy_back.config;

import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Repositery.RoleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes the database with default roles on application startup
 */
@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            // Initialize all roles if they don't exist
            if (roleRepository.findByRoleType(Role.RoleType.ADMIN).isEmpty()) {
                roleRepository.save(Role.builder()
                    .roleType(Role.RoleType.ADMIN)
                    .description("Administrator - Can manage all users and hotels")
                    .build());
            }

            if (roleRepository.findByRoleType(Role.RoleType.OWNER).isEmpty()) {
                roleRepository.save(Role.builder()
                    .roleType(Role.RoleType.OWNER)
                    .description("Hotel Owner - Can create hotels and services")
                    .build());
            }

            if (roleRepository.findByRoleType(Role.RoleType.GUEST).isEmpty()) {
                roleRepository.save(Role.builder()
                    .roleType(Role.RoleType.GUEST)
                    .description("Guest User - Read-only access to services")
                    .build());
            }

            if (roleRepository.findByRoleType(Role.RoleType.STAFF).isEmpty()) {
                roleRepository.save(Role.builder()
                    .roleType(Role.RoleType.STAFF)
                    .description("Staff Member - Created by owners")
                    .build());
            }

            if (roleRepository.findByRoleType(Role.RoleType.USER).isEmpty()) {
                roleRepository.save(Role.builder()
                    .roleType(Role.RoleType.USER)
                    .description("Normal User - Can create hotels and staff accounts")
                    .build());
            }
        };
    }
}
