package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.service.GameService;
import com.fiseq.truckcompany.service.GameServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

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
}
