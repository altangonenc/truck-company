package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.*;
import com.fiseq.truckcompany.dto.JobDto;
import com.fiseq.truckcompany.dto.TakeJobDto;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.entities.Job;
import com.fiseq.truckcompany.entities.Truck;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.entities.UserProfile;
import com.fiseq.truckcompany.exception.DifferentRegionDistanceCalculationException;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.exception.InvalidRouteForJobException;
import com.fiseq.truckcompany.exception.NotEnoughMoneyException;
import com.fiseq.truckcompany.repository.JobRepository;
import com.fiseq.truckcompany.repository.TruckRepository;
import com.fiseq.truckcompany.repository.UserProfileRepository;
import com.fiseq.truckcompany.repository.UserRepository;
import com.fiseq.truckcompany.utilities.DifferentRegionDistanceCalculator;
import com.fiseq.truckcompany.utilities.SameRegionDistanceCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService{
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TruckRepository truckRepository;
    private final JobRepository jobRepository;

    public GameServiceImpl(UserServiceImpl userService, UserRepository userRepository, UserProfileRepository userProfileRepository, TruckRepository truckRepository, JobRepository jobRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.truckRepository = truckRepository;
        this.jobRepository = jobRepository;
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
        UserProfile userProfile = user.getUserProfile();
        TruckModel truckModel = TruckModel.valueOf(truckName);

        if (checkUsersMoneyAndCompareItWithPriceOfTruck(truckModel.getPrice(), userProfile)) {
            Integer moneyLeft = userProfile.getTotalMoney() - truckModel.getPrice();
            userProfile.setTotalMoney(moneyLeft);
            userProfileRepository.save(userProfile);

            Truck truck = new Truck();
            truck.setTruckModel(truckModel);
            truck.setOwner(userProfile);
            truck.setOnTheJob(false);
            truckRepository.save(truck);

            TruckDto truckDto = new TruckDto();
            truckDto.setTruckModel(truckModel);
            truckDto.setMoneyLeftInAccount(moneyLeft);
            return truckDto;
        }

        throw new NotEnoughMoneyException();
    }

    public JobDto getAllJobsInTerminal(String token, String terminalName) throws InvalidAuthException {
        checkToken(token);
        FreightTerminals terminal = checkTerminalNameAndReturn(terminalName);
        JobDto jobDto = new JobDto();
        jobDto.setAllJobsInTerminal(jobRepository.findAllByOriginationTerminal(terminal));
        return jobDto;
    }

    public JobDto takeJob(String token, TakeJobDto takeJobDto, Long jobId) throws InvalidAuthException, DifferentRegionDistanceCalculationException, InvalidRouteForJobException {
        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = user.getUserProfile();
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        Job job = optionalJob.orElseThrow();

        double distance = 0;

        if (takeJobDto.getRoute() == null) {
            if (job.getOriginationTerminal().getRegion() != job.getDestinationTerminal().getRegion()) {
                DifferentRegionDistanceCalculator calculator = new DifferentRegionDistanceCalculator(job.getOriginationTerminal(),job.getDestinationTerminal());
                distance = calculator.calculateRoute();
            }
            if (job.getOriginationTerminal().getRegion() == job.getDestinationTerminal().getRegion()) {
                SameRegionDistanceCalculator calculator = new SameRegionDistanceCalculator(job.getOriginationTerminal(),job.getDestinationTerminal());
                distance = calculator.calculateRoute();
            }
        }

        if (takeJobDto.getRoute() != null) {
            if (FreightTerminals.valueOf(takeJobDto.getRoute()[0]) != job.getOriginationTerminal()
                    || FreightTerminals.valueOf(takeJobDto.getRoute()[takeJobDto.getRoute().length-1]) != job.getDestinationTerminal()) {
                throw new InvalidRouteForJobException(HttpStatus.BAD_REQUEST, GameErrorMessages.WRONG_ROUTE_FOR_THIS_JOB);
            }
            String[] route = takeJobDto.getRoute();
            for (int i=0; i < route.length-1; i++) {
                FreightTerminals from = FreightTerminals.valueOf(route[i]);
                FreightTerminals to = FreightTerminals.valueOf(route[i+1]);
                if (from.getRegion() != to.getRegion()) {
                    DifferentRegionDistanceCalculator calculator = new DifferentRegionDistanceCalculator(from,to);
                    distance = distance + calculator.calculateRoute();
                }
                if (from.getRegion() == to.getRegion()) {
                    SameRegionDistanceCalculator calculator = new SameRegionDistanceCalculator(from,to);
                    distance = distance + calculator.calculateRoute();
                }
            }
        }
        Truck truck = truckRepository.findById(takeJobDto.getTruckId()).orElseThrow();
        double speedOfTruck = truck.getTruckModel().getSpeedPerformance();
        LocalDateTime approximateCompletionOfJobTime = LocalDateTime.now().plusHours((long) distance / (long) speedOfTruck);

        job.setCompletionTime(approximateCompletionOfJobTime);
        job.setTruckOnTheJob(truck);
        job.setOwner(userProfile);
        job.setJobStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        JobDto jobDto = new JobDto();
        jobDto.setSuccessMessage(GameSuccessMessages.SUCCESSFULLY_TAKE_JOB.getUserText());
        return jobDto;
    }

    private FreightTerminals checkTerminalNameAndReturn(String terminalName) throws IllegalArgumentException{
        return FreightTerminals.valueOf(terminalName.toUpperCase());
    }

    private boolean checkUsersMoneyAndCompareItWithPriceOfTruck(Integer truckPrice, UserProfile userProfile) {

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
