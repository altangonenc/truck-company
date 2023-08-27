package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.dto.*;
import org.springframework.http.ResponseEntity;

public interface GameService {
    ResponseEntity<TruckDto> getAllTruckModels(String token);

    ResponseEntity<TruckDto> getTruckAttributes(String token, String truckModel);

    ResponseEntity<TruckDto> buyTruck(String token, String truckName, String location);

    ResponseEntity<JobDto> getAllJobsInTerminal(String token, String terminalName);

    ResponseEntity<JobDto> takeJob(String token, TakeJobDto takeJobDto, Long jobId);

    ResponseEntity<JobDto> finishJob(String token, Long jobId);

    ResponseEntity<JobDto> getAllJobs(String token);

    ResponseEntity<JobDto> getAllJobsForUser(String token);

    ResponseEntity<?> getAllTrucksOfUser(String token);

    ResponseEntity<ItemSellDto> sellItem(String authorizationHeader, ItemSellRequestDto itemSellRequestDto);

    ResponseEntity<MarketplaceDto> getAllItemsInMarketplace(String authorizationHeader, Double minPrice, Double maxPrice, String truckModel);

    ResponseEntity<ItemBuyDto> buyItem(String authorizationHeader, Long itemId);
}
