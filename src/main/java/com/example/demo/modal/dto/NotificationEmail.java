package com.example.demo.modal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class NotificationEmail {

    private String subject;
    private String recipient;
    private String body;

    public NotificationEmail() {
    }
}
