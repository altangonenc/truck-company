package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.dto.JobDto;
import com.fiseq.truckcompany.dto.TakeJobDto;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.exception.*;
import com.fiseq.truckcompany.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/trucks")
    public ResponseEntity<TruckDto> getAllTruckModels(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            ArrayList<TruckModel> truckModels = (ArrayList<TruckModel>) gameService.getAllTruckModels(authorizationHeader);
            TruckDto truckDto = new TruckDto();
            truckDto.setTruckModels(truckModels);
            return new ResponseEntity<>(truckDto, HttpStatus.OK);
        } catch (InvalidAuthException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(truckDto, e.getHttpStatus());
        } catch (Exception e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(truckDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/truck/attributes/{truckName}")
    public ResponseEntity<TruckDto> getSpecifiedTruckAttributes(@RequestHeader("Authorization") String authorizationHeader,
                                                                @PathVariable("truckName") String truckName) {
        try {
            TruckModel truckModel = gameService.getTruckAttributes(authorizationHeader, truckName);
            TruckDto truckDto = new TruckDto();
            truckDto.setTruckModel(truckModel);
            HashMap<String,Object> truckAttributes = new HashMap<>();
            truckAttributes.put("model",truckModel.getModel());
            truckAttributes.put("brand",truckModel.getBrand());
            truckAttributes.put("crashRisk",truckModel.getCrashRisk());
            truckAttributes.put("id",truckModel.getTruckId());
            truckAttributes.put("fuelPerformance",truckModel.getFuelConsumingPerformance());
            truckAttributes.put("speed",truckModel.getSpeedPerformance());
            truckAttributes.put("price",truckModel.getPrice());
            truckDto.setTruckModelAttributes(truckAttributes);
            return new ResponseEntity<>(truckDto, HttpStatus.OK);
        } catch (InvalidAuthException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(truckDto, e.getHttpStatus());
        } catch (IllegalArgumentException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(GameErrorMessages.GIVEN_TRUCK_MODEL_NOT_FOUND.getUserText());
            return new ResponseEntity<>(truckDto, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(truckDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/truck/buy/{truckName}")
    public ResponseEntity<TruckDto> buyTruck(@RequestHeader("Authorization") String authorizationHeader,
                                             @PathVariable("truckName") String truckName) {
        try {
            TruckDto truckDto = gameService.buyTruck(authorizationHeader, truckName);
            return new ResponseEntity<>(truckDto, HttpStatus.OK);
        } catch (InvalidAuthException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(truckDto, e.getHttpStatus());
        } catch (IllegalArgumentException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(GameErrorMessages.GIVEN_TRUCK_MODEL_NOT_FOUND.getUserText());
            return new ResponseEntity<>(truckDto, HttpStatus.NOT_FOUND);
        } catch (NotEnoughMoneyException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getGameErrorMessages().getUserText());
            return new ResponseEntity<>(truckDto, e.getHttpStatus());
        } catch (Exception e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(truckDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/jobs/{freightTerminal}")
    public ResponseEntity<JobDto> getAllJobsInTerminal(@RequestHeader("Authorization") String authorizationHeader,
                                                       @PathVariable("freightTerminal") String freightTerminal) {
        try {
            JobDto jobDto = gameService.getAllJobsInTerminal(authorizationHeader, freightTerminal);
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.GIVEN_TERMINAL_COUNTRY_NAME_NOT_FOUND.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.NOT_FOUND);
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

    @GetMapping("/jobs")
    public ResponseEntity<JobDto> getAllJobs(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            JobDto jobDto = gameService.getAllJobs(authorizationHeader);
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (InvalidAuthException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.THERE_IS_NO_VACANT_JOB.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (Exception e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(jobDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/jobs")
    public ResponseEntity<JobDto> getAllJobsForUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            JobDto jobDto = gameService.getAllJobsForUser(authorizationHeader);
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (InvalidAuthException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.THERE_IS_NO_VACANT_JOB.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (Exception e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(jobDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
}
