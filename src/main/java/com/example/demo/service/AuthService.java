package com.example.demo.service;

import com.example.demo.error.SpringRedditException;
import com.example.demo.modal.User;
import com.example.demo.modal.VerificationToken;
import com.example.demo.modal.dto.LoginRequest;
import com.example.demo.modal.dto.NotificationEmail;
import com.example.demo.modal.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.security.JWTProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JWTProvider jwtProvider;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository repository, VerificationTokenRepository tokenRepository, MailService mailService, UserRepository userRepository, AuthenticationManager authenticationManager, JWTProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
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

    public void verifyAccount(String token) {

        fetchUserAndEnable(tokenRepository.findByToken(token)
                .orElseThrow(() -> new SpringRedditException("invalid Token => " + token)));
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {

        User user = userRepository.findByUsername(verificationToken.getUser().getUsername())
                .orElseThrow(() -> new SpringRedditException("User not found with name - " +
                        verificationToken.getUser().getUsername()));

        user.setEnabled(true);
        userRepository.save(user);
    }

    public ResponseEntity<Object> login(LoginRequest request) {

        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                    request.getPassword()));

            return ResponseEntity.ok().build();

        } catch (AuthenticationException e){
            return ResponseEntity.badRequest().build();
        }

//        SecurityContextHolder.getContext().setAuthentication(authenticate);
//
//        String token = jwtProvider.generateToken(authenticate);
//
//        return new AuthenticationResponse(token, request.getUsername());
    }
}
