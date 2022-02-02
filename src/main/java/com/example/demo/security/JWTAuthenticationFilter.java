package com.example.demo.security;

import com.example.demo.modal.User;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;

    private final UserRepository userRepository;

    public JWTAuthenticationFilter(JWTProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String token = recoverToken(request);

            boolean validToken = jwtProvider.isTokenValid(token);

            if (validToken){
                authenticateUser(token);
            }

            filterChain.doFilter(request,response);
    }

    private void authenticateUser(String token) {
        String userId = jwtProvider.recoverUserId(token);
        User user = userRepository.findByEmail(userId).orElseThrow();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user,null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String recoverToken(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        return ((token == null) || token.isBlank() || !token.startsWith("Bearer ")) ? null : token.substring(7);
    }

}
