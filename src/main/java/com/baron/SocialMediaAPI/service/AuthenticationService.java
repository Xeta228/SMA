package com.baron.SocialMediaAPI.service;

import com.baron.SocialMediaAPI.dto.AuthenticationRequest;
import com.baron.SocialMediaAPI.dto.AuthenticationResponse;
import com.baron.SocialMediaAPI.dto.RegisterRequest;
import com.baron.SocialMediaAPI.exception.UserAlreadyExistsException;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {

        if (repository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();

    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );


        try {
            Authentication authentication = authenticationManager.authenticate(
                    token
            );

            var user = repository.findByUsername(request.getUsername()).orElseThrow();

            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .build();
        }
        catch (AuthenticationException ex) {
            throw new AuthenticationException("Invalid login or password") {

            };
        }

    }
}
