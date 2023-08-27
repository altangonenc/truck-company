package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
@Api(tags = "User Controller", description = "User Specific Operations")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationData> registerUser(@RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
        return userService.loginUser(loginForm);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserInformationDto> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        return userService.getUserProfile(authorizationHeader);
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
        return userService.changePassword(userDto);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboardForTotalMoney(@RequestHeader("Authorization") String authorizationHeader) {
        return userService.getLeaderboardForMoney(authorizationHeader);
    }
}
