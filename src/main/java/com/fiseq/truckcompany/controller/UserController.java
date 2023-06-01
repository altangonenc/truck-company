package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.constants.SecurityConstants;
import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationData> registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
        final String username = loginForm.getUsername();
        final String password = loginForm.getPassword();
        // verify username and password of user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create JWT and return it
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
                .compact();
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserInformationDto> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // should verify token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                UserInformationDto userInformationDtoError = new UserInformationDto();
                userInformationDtoError.setErrorMessage("Invalid authorization header");
                return new ResponseEntity<>(userInformationDtoError, HttpStatus.UNAUTHORIZED);
            }

            String token = authorizationHeader.substring(7); // "Bearer " prefixini kaldırıyoruz
            // verify token:
            if (!validateToken(token)) {
                UserInformationDto userInformationDtoError = new UserInformationDto();
                userInformationDtoError.setErrorMessage("Invalid token");
                return new ResponseEntity<>(userInformationDtoError, HttpStatus.UNAUTHORIZED);
            }

            // Token verified, return user
            String username = getUsernameFromToken(token);
            UserInformationDto userInformationDto = userService.getUserByUsername(username);
            if (userInformationDto == null) {
                UserInformationDto userInformationDtoError = new UserInformationDto();
                userInformationDtoError.setErrorMessage("User does not exist");
                return new ResponseEntity<>(userInformationDtoError, HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(userInformationDto);
        } catch (Exception e) {
            UserInformationDto userInformationDtoError = new UserInformationDto();
            userInformationDtoError.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(userInformationDtoError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateToken(String token) {
        try {
            String secretKey = SecurityConstants.SECRET;

            // resolve token and parse it to Claims
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();

            Date now = new Date();
            // check the token's expiration
            if (expirationDate.before(now)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // invalid token
            System.out.println(e.getMessage());
            return false;
        }
    }

    // extracts the username in token
    private String getUsernameFromToken(String token) {
        try {
            String secretKey = SecurityConstants.SECRET;

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // get username from Claims object
            String username = claims.getSubject();

            return username;
        } catch (Exception e) {
            return null;
        }
    }
}
