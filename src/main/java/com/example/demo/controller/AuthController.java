package com.example.demo.controller;

import com.example.demo.modal.dto.AuthenticationResponse;
import com.example.demo.modal.dto.LoginRequest;
import com.example.demo.modal.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.MailContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   private final AuthService authService;

   private final MailContentBuilder mailContentBuilder;

    @Autowired
    public AuthController(AuthService authService, MailContentBuilder mailContentBuilder) {
        this.authService = authService;
        this.mailContentBuilder = mailContentBuilder;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration Successful", OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> accountVerification(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>(mailContentBuilder.buildVerifyAccount("Account Activated Successfully"), OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request){

       return authService.login(request);

    }

}
