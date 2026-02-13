package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.DTO.AuthResponseDTO;
import org.bloomberg.keezy_back.DTO.CreateStaffDTO;
import org.bloomberg.keezy_back.DTO.LoginDTO;
import org.bloomberg.keezy_back.DTO.RegisterDTO;
import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Mapper.UserMapper;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;
import org.bloomberg.keezy_back.Repositery.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthenticationService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthenticationService(AppUserRepository userRepository, RoleRepository roleRepository,
                                 AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                 PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDTO register(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByRoleType(Role.RoleType.USER)
            .orElseThrow(() -> new RuntimeException("Default role not found"));

        AppUser user = AppUser.builder()
            .email(registerDTO.getEmail())
            .password(passwordEncoder.encode(registerDTO.getPassword()))
            .firstName(registerDTO.getFirstName())
            .lastName(registerDTO.getLastName())
            .phone(registerDTO.getPhone())
            .role(userRole)
            .enabled(true)
            .createdAt(System.currentTimeMillis())
            .build();

        AppUser savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public AuthResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                loginDTO.getPassword()
            )
        );

        AppUser user = userRepository.findByEmail(loginDTO.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtTokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getExpirationTime())
            .user(userMapper.toDTO(user))
            .build();
    }

    public UserDTO createStaff(CreateStaffDTO createStaffDTO, String userId) {
        AppUser currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Only ADMIN and USER roles can create staff
        if (!currentUser.getRole().getRoleType().equals(Role.RoleType.ADMIN)
            && !currentUser.getRole().getRoleType().equals(Role.RoleType.USER)) {
            throw new RuntimeException("Only admin and users can create staff accounts");
        }

        if (userRepository.existsByEmail(createStaffDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role staffRole = roleRepository.findByRoleType(Role.RoleType.STAFF)
            .orElseThrow(() -> new RuntimeException("Staff role not found"));

        AppUser staffUser = AppUser.builder()
            .email(createStaffDTO.getEmail())
            .password(passwordEncoder.encode(createStaffDTO.getPassword()))
            .firstName(createStaffDTO.getFirstName())
            .lastName(createStaffDTO.getLastName())
            .phone(createStaffDTO.getPhone())
            .role(staffRole)
            .enabled(true)
            .createdBy(currentUser.getEmail())
            .createdAt(System.currentTimeMillis())
            .build();

        AppUser savedStaff = userRepository.save(staffUser);
        return userMapper.toDTO(savedStaff);
    }
}




