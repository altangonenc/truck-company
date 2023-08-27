package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface UserService {
    ResponseEntity<UserRegistrationData> registerUser(UserDto userDto);

    ResponseEntity<UserInformationDto> getUserProfile(String authorizationHeader);

    String extractTokenAndGetUsername(String authorizationHeader) throws InvalidAuthException;

    ArrayList<String> getRecoveryQuestion(String authorizationHeader);

    ResponseEntity<UserInformationDto> changePassword(UserDto userDto);

    boolean isPasswordMatched (String password, String username);

    ResponseEntity<?> getLeaderboardForMoney(String authorizationHeader);

    ResponseEntity<String> loginUser(LoginForm loginForm);
}
