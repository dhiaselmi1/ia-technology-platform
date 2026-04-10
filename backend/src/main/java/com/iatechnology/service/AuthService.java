package com.iatechnology.service;

import com.iatechnology.dto.auth.AuthResponse;
import com.iatechnology.dto.auth.LoginRequest;
import com.iatechnology.dto.auth.RegisterRequest;
import com.iatechnology.model.Role;
import com.iatechnology.model.User;
import com.iatechnology.repository.UserRepository;
import com.iatechnology.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.UTILISATEUR)
                .build();

        userRepository.save(user);
        auditLogService.log("REGISTER", "User", null, user.getEmail(), "New user registered");

        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        auditLogService.log("LOGIN", "User", user.getId(), user.getEmail(), "User logged in");

        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        return AuthResponse.builder()
                .token(jwtTokenProvider.generateToken(user))
                .role(user.getRole().name())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
