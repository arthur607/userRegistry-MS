package com.example.demo.service;

import com.example.demo.modal.User;
import com.example.demo.modal.VerificationToken;
import com.example.demo.modal.dto.NotificationEmail;
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

    private final MailService mailService;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository repository, VerificationTokenRepository tokenRepository, MailService mailService) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
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

        mailService.sendMail(new NotificationEmail("Please Active your Account",
                save.getEmail(),"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8089/api/auth/accountVerification/" + token));

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
