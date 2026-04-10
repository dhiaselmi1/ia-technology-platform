package com.iatechnology.service;

import com.iatechnology.dto.UserDTO;
import com.iatechnology.model.Role;
import com.iatechnology.model.User;
import com.iatechnology.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public UserDTO create(UserDTO dto, String adminEmail) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode("DefaultPassword123!"))
                .role(dto.getRole() != null ? dto.getRole() : Role.UTILISATEUR)
                .active(true)
                .build();
        User saved = userRepository.save(user);
        auditLogService.log("CREATE", "User", saved.getId(), adminEmail, "User created: " + saved.getEmail());
        return toDTO(saved);
    }

    @Transactional
    public UserDTO updateRole(Long id, Role role, String adminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setRole(role);
        User updated = userRepository.save(user);
        auditLogService.log("UPDATE", "User", id, adminEmail, "User role updated to " + role.name());
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id, String adminEmail) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
        auditLogService.log("DELETE", "User", id, adminEmail, "User deleted");
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
