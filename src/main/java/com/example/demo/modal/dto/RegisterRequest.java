package com.example.demo.modal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {

    private String email;
    private String username;
    private String password;

    public RegisterRequest() {
    }
}
