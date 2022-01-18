package com.example.demo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String token = recoverToken(request);

            log.info(token);

            filterChain.doFilter(request,response);
    }

    private String recoverToken(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        return ((token == null) || token.isBlank() || !token.startsWith("Bearer ")) ? null : token.substring(7);
    }

}
