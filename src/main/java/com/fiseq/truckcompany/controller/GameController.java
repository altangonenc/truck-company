package com.fiseq.truckcompany.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fiseq.truckcompany.dto.*;
import com.fiseq.truckcompany.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
@Api(tags = "Game Controller", description = "In Game Operations")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/trucks")
    @JsonView(Views.TruckModelView.class)
    public ResponseEntity<TruckDto> getAllTruckModels(@RequestHeader("Authorization") String authorizationHeader) {
        return gameService.getAllTruckModels(authorizationHeader);
    }

    @GetMapping("/truck/attributes/{truckName}")
    public ResponseEntity<TruckDto> getSpecifiedTruckAttributes(@RequestHeader("Authorization") String authorizationHeader,
                                                                @PathVariable("truckName") String truckName) {
        return gameService.getTruckAttributes(authorizationHeader, truckName);
    }

    @PostMapping("/truck/buy/{truckName}/{location}")
    public ResponseEntity<TruckDto> buyTruck(@RequestHeader("Authorization") String authorizationHeader,
                                             @PathVariable("truckName") String truckName,
                                             @PathVariable("location") String location) {
        return gameService.buyTruck(authorizationHeader, truckName, location);
    }

    @GetMapping("/truck/get/all")
    public ResponseEntity<?> getAllTrucksOfUser(@RequestHeader("Authorization") String authorizationHeader) {
        return gameService.getAllTrucksOfUser(authorizationHeader);
    }

    @GetMapping("/jobs/{freightTerminal}")
    public ResponseEntity<JobDto> getAllJobsInTerminal(@RequestHeader("Authorization") String authorizationHeader,
                                                       @PathVariable("freightTerminal") String freightTerminal) {
        return gameService.getAllJobsInTerminal(authorizationHeader, freightTerminal);
    }

    @ApiOperation(value = "Get All Jobs for all terminals", notes = "Returns all the jobs available.")
    @GetMapping("/jobs")
    public ResponseEntity<JobDto> getAllJobs(@RequestHeader("Authorization") String authorizationHeader) {
        return gameService.getAllJobs(authorizationHeader);
    }

    @GetMapping("/user/jobs")
    public ResponseEntity<JobDto> getAllJobsForUser(@RequestHeader("Authorization") String authorizationHeader) {
        return gameService.getAllJobsForUser(authorizationHeader);
    }

    @PostMapping("/job/{id}/take")
    public ResponseEntity<JobDto> takeJob(@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable("id") Long jobId,
                                          @RequestBody(required = false) TakeJobDto takeJobDto) {
        return gameService.takeJob(authorizationHeader, takeJobDto, jobId);
    }

    @PostMapping("/job/{id}/finish")
    public ResponseEntity<JobDto> finishJob(@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable("id") Long jobId) {
        return gameService.finishJob(authorizationHeader, jobId);
    }

    @PostMapping("/item/sell")
    public ResponseEntity<ItemSellDto> sellItem(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody ItemSellRequestDto itemSellRequestDto) {
        return gameService.sellItem(authorizationHeader, itemSellRequestDto);
    }

    @GetMapping("/item/marketplace")
    public ResponseEntity<MarketplaceDto> getAllItemsInMarketplace(@RequestHeader("Authorization") String authorizationHeader,
                                                                   @RequestParam(required = false) Double minPrice,
                                                                   @RequestParam(required = false) Double maxPrice,
                                                                   @RequestParam(required = false) String truckModel) {
        return gameService.getAllItemsInMarketplace(authorizationHeader, minPrice, maxPrice, truckModel);
    }

    @PostMapping("/item/{id}/buy")
    public ResponseEntity<ItemBuyDto> buyItem(@RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable("id") Long itemId) {
        return gameService.buyItem(authorizationHeader, itemId);
    }

}
