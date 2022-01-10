package com.example.demo.service;

import com.example.demo.modal.User;
import com.example.demo.modal.VerificationToken;
import com.example.demo.modal.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository repository;

    private final VerificationTokenRepository tokenRepository;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository repository, VerificationTokenRepository tokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void signup(RegisterRequest request){
        User save = repository.save(User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .created(Instant.now())
                .enabled(false)
                .build()
        );
        String token = generateVerificationToken(save);

    }

    private String generateVerificationToken(User user){

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        tokenRepository.save(verificationToken);

        return token;
    }

}
