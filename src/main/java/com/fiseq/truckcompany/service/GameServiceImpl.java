package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.entities.Truck;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.entities.UserProfile;
import com.fiseq.truckcompany.exception.CannotBuyTruckException;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.exception.NotEnoughMoneyException;
import com.fiseq.truckcompany.repository.TruckRepository;
import com.fiseq.truckcompany.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository;
    private final TruckRepository truckRepository;

    public GameServiceImpl(UserServiceImpl userService, UserRepository userRepository, UserProfileRepository userProfileRepository, TruckRepository truckRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.truckRepository = truckRepository;
    }

    public List<TruckModel> getAllTruckModels(String token) throws InvalidAuthException {
        checkToken(token);
        return new ArrayList<>(Arrays.asList(TruckModel.values()));
    }

    public TruckModel getTruckAttributes(String token, String truckModel) throws InvalidAuthException, IllegalArgumentException {
        checkToken(token);
        return TruckModel.valueOf(truckModel);
    }

    public TruckDto buyTruck(String token, String truckName) throws Exception {

        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = userProfileRepository.findByUser(user);
        TruckModel truckModel = TruckModel.valueOf(truckName);

        if (checkUsersMoneyAndCompareItWithPriceOfTruck(truckModel.getPrice(), userProfile)) {
            Integer moneyLeft = userProfile.getTotalMoney() - truckModel.getPrice();
            userProfile.setTotalMoney(moneyLeft);
            userProfileRepository.save(userProfile);

            Truck truck = new Truck();
            truck.setTruckModel(truckModel);
            truck.setOwner(userProfile);
            truckRepository.save(truck);

            TruckDto truckDto = new TruckDto();
            truckDto.setTruckModel(truckModel);
            truckDto.setMoneyLeftInAccount(moneyLeft);
            return truckDto;
        }

        throw new NotEnoughMoneyException();
    }

    public boolean checkUsersMoneyAndCompareItWithPriceOfTruck(Integer truckPrice, UserProfile userProfile) {

        if (truckPrice <= userProfile.getTotalMoney()) {
            return true;
        }
        return false;
    }

    private void checkToken(String token) throws InvalidAuthException {
        if (!isTokenValid(token)) {
            throw new InvalidAuthException(HttpStatus.UNAUTHORIZED, UserRegistrationErrorMessages.INVALID_AUTH_PARAMETERS);
        }
    }

    private String checkTokenAndReturnUsername(String token) throws InvalidAuthException {
        return userService.extractTokenAndGetUsername(token);
    }

    private boolean isTokenValid(String token) throws InvalidAuthException {
        String username = userService.extractTokenAndGetUsername(token);
        if (userRepository.existsByUserName(username)) {
            return true;
        }
        return false;
    }
}
