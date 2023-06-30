package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GameServiceImpl implements GameService{
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    public GameServiceImpl(UserServiceImpl userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<TruckModel> getAllTruckModels(String token) throws InvalidAuthException {
        checkToken(token);
        return new ArrayList<>(Arrays.asList(TruckModel.values()));
    }

    private void checkToken(String token) throws InvalidAuthException {
        if (!isTokenValid(token)) {
            throw new InvalidAuthException(HttpStatus.UNAUTHORIZED, UserRegistrationErrorMessages.INVALID_AUTH_PARAMETERS);
        }
    }

    private boolean isTokenValid(String token) throws InvalidAuthException {
        String username = userService.extractTokenAndGetUsername(token);
        if (userRepository.existsByUserName(username)) {
            return true;
        }
        return false;
    }
}
