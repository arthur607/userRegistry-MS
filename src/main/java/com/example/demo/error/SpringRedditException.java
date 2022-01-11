package com.example.demo.error;

import org.springframework.mail.MailException;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String s, MailException e) {
    }
}
