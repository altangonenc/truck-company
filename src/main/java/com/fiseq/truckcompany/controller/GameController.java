package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.constants.GameErrorMessages;
import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.exception.CannotBuyTruckException;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.exception.NotEnoughMoneyException;
import com.fiseq.truckcompany.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

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
}
