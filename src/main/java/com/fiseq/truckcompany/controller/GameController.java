package com.fiseq.truckcompany.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fiseq.truckcompany.constants.GameErrorMessages;
import com.fiseq.truckcompany.dto.*;
import com.fiseq.truckcompany.exception.*;
import com.fiseq.truckcompany.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/game")
@Api(tags = "Game Controller", description = "In Game Operations")
public class GameController {
    private final GameService gameService;

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
        try {
            JobDto jobDto = gameService.takeJob(authorizationHeader, takeJobDto, jobId);
            return new ResponseEntity<>(jobDto, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.GIVEN_JOB_ID_OR_TRUCK_ID_INVALID.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.BAD_REQUEST);
        } catch (OutdatedTruckException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, e.getHttpStatus());
        } catch (IllegalArgumentException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.GIVEN_TERMINAL_NAMES_IN_ROUTE_NOT_VALID.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.BAD_REQUEST);
        } catch (InvalidAuthException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.UNAUTHORIZED);
        } catch (InvalidRouteForJobException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, e.getHttpStatus());
        } catch (Exception e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(jobDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/job/{id}/finish")
    public ResponseEntity<JobDto> finishJob(@RequestHeader("Authorization") String authorizationHeader,
                                    @PathVariable("id") Long jobId) {
        try {
            JobDto jobDto = gameService.finishJob(authorizationHeader, jobId);
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (JobIsNotFinishedException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            jobDto.setRemainingTime(e.getRemainingTime());
            return new ResponseEntity<>(jobDto, e.getHttpStatus());
        } catch (TruckCrashedException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, e.getHttpStatus());
        } catch (NoSuchElementException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.GIVEN_JOB_ID_OR_TRUCK_ID_INVALID.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.BAD_REQUEST);
        } catch (InvalidAuthException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(jobDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/item/sell")
    public ResponseEntity<ItemSellDto> sellItem(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody ItemSellRequestDto itemSellRequestDto) {
        try {
            ItemSellDto itemSellDto = gameService.sellItem(authorizationHeader, itemSellRequestDto);
            return new ResponseEntity<>(itemSellDto, HttpStatus.CREATED);
        } catch (InvalidAuthException e) {
            ItemSellDto itemSellDto = new ItemSellDto();
            itemSellDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(itemSellDto, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            ItemSellDto itemSellDto = new ItemSellDto();
            itemSellDto.setErrorMessage(GameErrorMessages.GIVEN_TRUCK_ID_INVALID.getUserText());
            return new ResponseEntity<>(itemSellDto, HttpStatus.BAD_REQUEST);
        } catch (IncorrectPricingException e) {
            ItemSellDto itemSellDto = new ItemSellDto();
            itemSellDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(itemSellDto, e.getHttpStatus());
        } catch (Exception e) {
            ItemSellDto itemSellDto = new ItemSellDto();
            itemSellDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(itemSellDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/item/marketplace")
    public ResponseEntity<MarketplaceDto> getAllItemsInMarketplace(@RequestHeader("Authorization") String authorizationHeader,
                                                                   @RequestParam(required = false) Double minPrice,
                                                                   @RequestParam(required = false) Double maxPrice,
                                                                   @RequestParam(required = false) String truckModel) {
        try {
            MarketplaceDto marketplaceDto = gameService.getAllItemsInMarketplace(authorizationHeader, minPrice, maxPrice, truckModel);
            return new ResponseEntity<>(marketplaceDto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            MarketplaceDto marketplaceDto = new MarketplaceDto();
            marketplaceDto.setErrorMessage(GameErrorMessages.GIVEN_TRUCK_MODEL_NOT_FOUND.getUserText());
            return new ResponseEntity<>(marketplaceDto, HttpStatus.NOT_FOUND);
        } catch (InvalidAuthException e) {
            MarketplaceDto marketplaceDto = new MarketplaceDto();
            marketplaceDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(marketplaceDto, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            MarketplaceDto marketplaceDto = new MarketplaceDto();
            marketplaceDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(marketplaceDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/item/{id}/buy")
    public ResponseEntity<ItemBuyDto> buyItem(@RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable("id") Long itemId) {
        try {
            ItemBuyDto itemBuyDto = gameService.buyItem(authorizationHeader, itemId);
            return new ResponseEntity<>(itemBuyDto, HttpStatus.CREATED);
        } catch (CannotBuyTruckException e) {
            ItemBuyDto itemBuyDto = new ItemBuyDto();
            itemBuyDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(itemBuyDto, e.getHttpStatus());
        } catch (NotEnoughMoneyException e) {
            ItemBuyDto itemBuyDto = new ItemBuyDto();
            itemBuyDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(itemBuyDto, e.getHttpStatus());
        } catch (NoSuchElementException e) {
            ItemBuyDto itemBuyDto = new ItemBuyDto();
            itemBuyDto.setErrorMessage(GameErrorMessages.GIVEN_TRUCK_ID_INVALID.getUserText());
            return new ResponseEntity<>(itemBuyDto, HttpStatus.BAD_REQUEST);
        } catch (InvalidAuthException e) {
            ItemBuyDto itemBuyDto = new ItemBuyDto();
            itemBuyDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(itemBuyDto, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            ItemBuyDto itemBuyDto = new ItemBuyDto();
            itemBuyDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(itemBuyDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
