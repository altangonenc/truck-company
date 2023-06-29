package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GameService {
    private final UserService userService;

    public GameService(UserService userService) {
        this.userService = userService;
    }

    public List<TruckModel> getAllTruckModels(String token) throws InvalidAuthException {
        userService.extractTokenAndGetUsername(token);
        return new ArrayList<>(Arrays.asList(TruckModel.values()));
    }
}
