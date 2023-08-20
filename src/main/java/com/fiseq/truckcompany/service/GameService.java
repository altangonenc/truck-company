package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.dto.*;
import com.fiseq.truckcompany.exception.*;

import java.util.List;

public interface GameService {
    List<TruckModel> getAllTruckModels(String token) throws InvalidAuthException;

    TruckModel getTruckAttributes(String token, String truckModel) throws InvalidAuthException, IllegalArgumentException;

    TruckDto buyTruck(String token, String truckName, String location) throws Exception;

    JobDto getAllJobsInTerminal(String token, String terminalName) throws InvalidAuthException;

    JobDto takeJob(String token, TakeJobDto takeJobDto, Long jobId) throws InvalidAuthException, DifferentRegionDistanceCalculationException, InvalidRouteForJobException;

    JobDto finishJob(String token, Long jobId) throws InvalidAuthException, JobIsNotFinishedException, TruckCrashedException;

    JobDto getAllJobs(String token) throws InvalidAuthException;

    JobDto getAllJobsForUser(String token) throws InvalidAuthException;

    List<TruckDto> getAllTrucksOfUser(String token) throws InvalidAuthException;

    ItemSellDto sellItem(String authorizationHeader, ItemDto itemDto);
}
