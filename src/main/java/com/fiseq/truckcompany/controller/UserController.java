package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.constants.SecurityConstants;
import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.exception.ChangePasswordException;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/users")
@Api(tags = "User Controller", description = "User Specific Operations")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationData> registerUser(@RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
        final String username = loginForm.getUsername();
        final String password = loginForm.getPassword();
        if (!userService.isPasswordMatched(password, username)) {
            return new ResponseEntity<>(UserRegistrationErrorMessages.INVALID_AUTH_PARAMETERS.getUserText(), HttpStatus.BAD_REQUEST);

        }
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
            return userService.getUserProfile(authorizationHeader);

        } catch (InvalidAuthException e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(userInformationDto, e.getHttpStatus());
        } catch (Exception e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(userInformationDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Get all the questions in system for recovery if you send request without TOKEN.
     * If you send request with correct TOKEN of user you will get specified user's recovery question.
     */
    @GetMapping("/recovery-question")
    public ResponseEntity<ArrayList<String>> getRecoveryQuestion(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        ArrayList<String> questionList = userService.getRecoveryQuestion(authorizationHeader);
        return new ResponseEntity<>(questionList, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserInformationDto> changePassword(@RequestBody UserDto userDto) {
        try {
            return userService.changePassword(userDto);

        } catch (ChangePasswordException e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(userInformationDto, e.getHttpStatus());
        } catch (Exception e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage("Something went wrong while changing password.");
            return new ResponseEntity<>(userInformationDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
