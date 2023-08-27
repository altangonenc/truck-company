package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.dto.LeaderboardDto;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.exception.ChangePasswordException;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface UserService {
    ResponseEntity<UserRegistrationData> registerUser(UserDto userDto);

    ResponseEntity<UserInformationDto> getUserProfile(String authorizationHeader) throws InvalidAuthException;

    String extractTokenAndGetUsername(String authorizationHeader) throws InvalidAuthException;

    ArrayList<String> getRecoveryQuestion(String authorizationHeader);

    ResponseEntity<UserInformationDto> changePassword(UserDto userDto) throws ChangePasswordException;

    boolean isPasswordMatched (String password, String username);

    List<LeaderboardDto> getLeaderboardForMoney(String authorizationHeader);
}
