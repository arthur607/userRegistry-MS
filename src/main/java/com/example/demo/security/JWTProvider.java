package com.example.demo.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static java.util.Date.from;

@Service
public class JWTProvider {

    private KeyStore keyStore;

    private final String secretToken = UUID.randomUUID().toString();


    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SecurityException("Exception occurred while loading keystore", e);
        }

    }

    public String  generateToken(Authentication authentication){
        var logado = (User) authentication.getPrincipal();

        Date today = new Date();
        Date dateExpiration = new Date(today.getTime() + 900000);

        return Jwts.builder()
                .setIssuer("Arthur's API")
                .setSubject(logado.getUsername())
                .setIssuedAt(from(Instant.now()))
                .setExpiration(dateExpiration)
                .signWith(SignatureAlgorithm.HS256, secretToken)
                .compact();
    }


    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SecurityException("Exception occured while retrieving public key from keystore", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secretToken).parseClaimsJws(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }


    public String recoverUserId(String token) {
        Claims body = Jwts.parser().setSigningKey(secretToken).parseClaimsJws(token).getBody();

       return body.getSubject();
    }
}
