package com.bond.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bond.dto.user.UserLoginRequestDto;
import com.bond.dto.user.UserLoginResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Verify that authenticate method works fine with valid input")
    void authenticate_ValidRequest_ReturnsValidToken() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password");

        // Mock authentication result
        // Mock JWT token generation
        Authentication authentication = mock(Authentication.class);
        String token = "super_secret_token125u23y65021360fsudit`g783w65";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(authentication.getName())).thenReturn(token);

        // Execute the method
        UserLoginResponseDto responseDto = authenticationService.authenticate(requestDto);

        // Verify that authenticationManager.authenticate and jwtUtil.generateToken were called
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(), requestDto.getPassword())
                );
        verify(jwtUtil, times(1)).generateToken(authentication.getName());

        assertNotNull(responseDto);
        assertEquals(token, responseDto.getToken());
    }

    @Test
    @DisplayName("Verify that authenticate() method works as expected with non-valid input")
    void authenticate_InvalidInput_ThrowsException() {
        // Provide invalid input
        UserLoginRequestDto requestDto = new UserLoginRequestDto();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticate(requestDto));

        // Verify that authenticationManager.authenticate was called
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Ensure jwtUtil.generateToken is not called
        verify(jwtUtil, never()).generateToken(any());
    }
}
