package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Mapper.UserMapper;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;
import org.bloomberg.keezy_back.Repositery.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, RoleRepository roleRepository,
                      UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserById(String id) {
        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userMapper.toDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByRole(String roleName) {
        Role.RoleType roleType = Role.RoleType.valueOf(roleName);
        return userRepository.findByRoleRoleType(roleType).stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
    }

    public UserDTO updateUser(String id, UserDTO userDTO, String currentUserId) {
        AppUser currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Only admin or the user themselves can update
        if (!currentUser.getRole().getRoleType().equals(Role.RoleType.ADMIN)
            && !currentUser.getId().equals(user.getId())) {
            throw new RuntimeException("You don't have permission to update this user");
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setUpdatedBy(currentUser.getEmail());
        user.setUpdatedAt(System.currentTimeMillis());

        AppUser updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public void deleteUser(String id, String currentUserId) {
        AppUser currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Only admin can delete users
        if (!currentUser.getRole().getRoleType().equals(Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can delete users");
        }

        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userRepository.delete(user);
    }

    public UserDTO disableUser(String id, String currentUserId) {
        AppUser currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Only admin can disable users
        if (!currentUser.getRole().getRoleType().equals(Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can disable users");
        }

        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEnabled(false);
        user.setUpdatedBy(currentUser.getEmail());
        user.setUpdatedAt(System.currentTimeMillis());

        AppUser updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public UserDTO enableUser(String id, String currentUserId) {
        AppUser currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Only admin can enable users
        if (!currentUser.getRole().getRoleType().equals(Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can enable users");
        }

        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEnabled(true);
        user.setUpdatedBy(currentUser.getEmail());
        user.setUpdatedAt(System.currentTimeMillis());

        AppUser updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }
}






