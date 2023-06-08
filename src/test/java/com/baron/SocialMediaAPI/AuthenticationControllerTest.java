package com.baron.SocialMediaAPI;

import com.baron.SocialMediaAPI.controller.AuthenticationController;
import com.baron.SocialMediaAPI.dto.AuthenticationRequest;
import com.baron.SocialMediaAPI.dto.AuthenticationResponse;
import com.baron.SocialMediaAPI.dto.RegisterRequest;
import com.baron.SocialMediaAPI.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Validator validator;

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(authenticationService, validator);
    }

    @Test
    void register_withValidRegisterRequest_shouldReturnOkResponse() {
        RegisterRequest registerRequest = new RegisterRequest();

        AuthenticationResponse expectedResponse = new AuthenticationResponse();


        when(authenticationService.register(registerRequest)).thenReturn(expectedResponse);


        ResponseEntity<AuthenticationResponse> response = authenticationController.register(registerRequest);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());


        verify(authenticationService).register(registerRequest);
    }

    @Test
    void login_withValidAuthenticationRequest_shouldReturnOkResponse() {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();


        AuthenticationResponse expectedResponse = new AuthenticationResponse();

        when(authenticationService.authenticate(authenticationRequest)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.login(authenticationRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(authenticationService).authenticate(authenticationRequest);
    }
}