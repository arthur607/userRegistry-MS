package com.example.demo.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;

@Getter
public class SpringRedditException extends RuntimeException {
    
    private final Integer codigo;
    
    private final String mensagem;

    public SpringRedditException(String message, Throwable cause, Integer codigo, String mensagem) {
        super(message, cause);
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public SpringRedditException(String msgError, MailException e) {
        this.mensagem = msgError;
        this.codigo = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public SpringRedditException(String msgError) {
        this.mensagem = msgError;
        this.codigo = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
