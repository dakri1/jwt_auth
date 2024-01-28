package com.example.library.controllers;


import com.example.library.dtos.JwtRequest;
import com.example.library.dtos.JwtResponse;
import com.example.library.dtos.RegistrationUserDto;
import com.example.library.exceptions.AppError;
import com.example.library.service.AuthService;
import com.example.library.service.UserService;
import com.example.library.utils.JwtTokensUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokensUtils jwtTokensUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;


    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Username or password is incorrect"),
                    HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokensUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));

    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }
}
