package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Repositery.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializationService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        // Initialize ADMIN role
        if (roleRepository.findByRoleType(Role.RoleType.ADMIN).isEmpty()) {
            Role adminRole = Role.builder()
                .roleType(Role.RoleType.ADMIN)
                .description(Role.RoleType.ADMIN.getDescription())
                .build();
            roleRepository.save(adminRole);
        }

        // Initialize USER role
        if (roleRepository.findByRoleType(Role.RoleType.USER).isEmpty()) {
            Role userRole = Role.builder()
                .roleType(Role.RoleType.USER)
                .description(Role.RoleType.USER.getDescription())
                .build();
            roleRepository.save(userRole);
        }

        // Initialize STAFF role
        if (roleRepository.findByRoleType(Role.RoleType.STAFF).isEmpty()) {
            Role staffRole = Role.builder()
                .roleType(Role.RoleType.STAFF)
                .description(Role.RoleType.STAFF.getDescription())
                .build();
            roleRepository.save(staffRole);
        }
    }
}


