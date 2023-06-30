package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.exception.InvalidAuthException;

import java.util.List;

public interface GameService {
    List<TruckModel> getAllTruckModels(String token) throws InvalidAuthException;

    TruckModel getTruckAttributes(String token, String truckModel) throws InvalidAuthException, IllegalArgumentException;
}
