package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.dto.*;
import com.fiseq.truckcompany.exception.*;
import org.springframework.http.ResponseEntity;

public interface GameService {
    ResponseEntity<TruckDto> getAllTruckModels(String token) throws InvalidAuthException;

    ResponseEntity<TruckDto> getTruckAttributes(String token, String truckModel) throws InvalidAuthException, IllegalArgumentException;

    ResponseEntity<TruckDto> buyTruck(String token, String truckName, String location);

    ResponseEntity<JobDto> getAllJobsInTerminal(String token, String terminalName);

    JobDto takeJob(String token, TakeJobDto takeJobDto, Long jobId) throws InvalidAuthException, DifferentRegionDistanceCalculationException, InvalidRouteForJobException;

    JobDto finishJob(String token, Long jobId) throws InvalidAuthException, JobIsNotFinishedException, TruckCrashedException;

    ResponseEntity<JobDto> getAllJobs(String token);

    ResponseEntity<JobDto> getAllJobsForUser(String token);

    ResponseEntity<?> getAllTrucksOfUser(String token) throws InvalidAuthException;

    ItemSellDto sellItem(String authorizationHeader, ItemSellRequestDto itemSellRequestDto);

    MarketplaceDto getAllItemsInMarketplace(String authorizationHeader, Double minPrice, Double maxPrice, String truckModel);

    ItemBuyDto buyItem(String authorizationHeader, Long itemId);
}
