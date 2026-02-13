package org.bloomberg.keezy_back.Repositery;

import org.bloomberg.keezy_back.Entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    List<AppUser> findByRoleRoleType(org.bloomberg.keezy_back.Entity.Role.RoleType roleType);
}


