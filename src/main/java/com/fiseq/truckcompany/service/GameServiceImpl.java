package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.*;
import com.fiseq.truckcompany.dto.JobDto;
import com.fiseq.truckcompany.dto.RemainingTime;
import com.fiseq.truckcompany.dto.TakeJobDto;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.entities.Job;
import com.fiseq.truckcompany.entities.Truck;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.entities.UserProfile;
import com.fiseq.truckcompany.exception.*;
import com.fiseq.truckcompany.repository.JobRepository;
import com.fiseq.truckcompany.repository.TruckRepository;
import com.fiseq.truckcompany.repository.UserProfileRepository;
import com.fiseq.truckcompany.repository.UserRepository;
import com.fiseq.truckcompany.utilities.DifferentRegionDistanceCalculator;
import com.fiseq.truckcompany.utilities.SameRegionDistanceCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
            double moneyLeft = userProfile.getTotalMoney() - truckModel.getPrice();
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

    public JobDto getAllJobs(String token) throws InvalidAuthException {
        checkToken(token);
        JobDto jobDto = new JobDto();
        List<Job> jobs = jobRepository.findAllByJobStatusEquals(JobStatus.VACANT).orElseThrow();
        jobDto.setAllJobs(jobs);
        return jobDto;
    }

    public JobDto getAllJobsForUser(String token) throws InvalidAuthException {
        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = user.getUserProfile();
        JobDto jobDto = new JobDto();
        List<Job> jobs = jobRepository.findAllByJobStatusEqualsAndOwnerEquals(JobStatus.VACANT, userProfile).orElseThrow();
        jobDto.setAllJobs(jobs);
        return jobDto;
    }

    public List<TruckDto> getAllTrucksOfUser(String token) throws InvalidAuthException {
        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = user.getUserProfile();
        ArrayList<Truck> trucks = truckRepository.findAllByOwner(userProfile).orElseThrow();
        ArrayList<TruckDto> allTheTruckDtos = new ArrayList<>();
        for (Truck truck : trucks) {

            TruckDto truckDto = new TruckDto();
            HashMap<String, Object> attributes = new HashMap<>();
            attributes.put("model",truck.getTruckModel().getModel());
            attributes.put("brand",truck.getTruckModel().getBrand());
            attributes.put("crashRisk",truck.getTruckModel().getCrashRisk());
            attributes.put("id",truck.getTruckModel().getTruckModelId());
            attributes.put("fuelPerformance",truck.getTruckModel().getFuelConsumingPerformance());
            attributes.put("speed",truck.getTruckModel().getSpeedPerformance());
            attributes.put("price",truck.getTruckModel().getPrice());
            truckDto.setTruckModelAttributes(attributes);
            truckDto.setTruckId(truck.getId());
            truckDto.setOnTheJob(truck.isOnTheJob());

            allTheTruckDtos.add(truckDto);
        }

        return allTheTruckDtos;
    }

    public JobDto takeJob(String token, TakeJobDto takeJobDto, Long jobId) throws InvalidAuthException, DifferentRegionDistanceCalculationException, InvalidRouteForJobException {
        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = user.getUserProfile();
        Optional<Job> optionalJob = jobRepository.findByIdAndOwnerEquals(jobId, null);
        Job job = optionalJob.orElseThrow();

        double distance = calculateDistance(takeJobDto, job);

        Truck truck = truckRepository.findByIdAndOnTheJob(takeJobDto.getTruckId(), false).orElseThrow();
        double speedOfTruck = truck.getTruckModel().getSpeedPerformance();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime approximateCompletionOfJobTime = now.plusHours((long) distance / (long) speedOfTruck);

        truck.setOnTheJob(true);
        truckRepository.save(truck);

        job.setCompletionTime(approximateCompletionOfJobTime);
        job.setStartedTime(now);
        job.setTruckOnTheJob(truck);
        job.setOwner(userProfile);
        job.setJobStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        JobDto jobDto = new JobDto();
        jobDto.setSuccessMessage(GameSuccessMessages.SUCCESSFULLY_TAKE_JOB.getUserText());
        return jobDto;
    }

    public JobDto finishJob(String token, Long jobId) throws InvalidAuthException, JobIsNotFinishedException, TruckCrashedException {
        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = user.getUserProfile();
        Optional<Job> optionalJob = jobRepository.findByIdAndOwnerEquals(jobId, userProfile);
        Job job = optionalJob.orElseThrow();
        Truck truck = job.getTruckOnTheJob();

        Duration durationBetweenStartedAndFinishedTime = Duration.between(job.getCompletionTime(), job.getStartedTime());
        long minutes = durationBetweenStartedAndFinishedTime.toMinutes();

        if (job.getJobStatus() == JobStatus.VACANT) {
            throw new JobIsNotFinishedException(GameErrorMessages.JOB_IS_IN_VACANT_STATUS);
        }
        if (job.getCompletionTime().isAfter(LocalDateTime.now())) {
            RemainingTime remainingTime = new RemainingTime(job);
            throw new JobIsNotFinishedException(remainingTime, GameErrorMessages.JOB_IS_NOT_FINISHED);
        }

        if (isTruckCrashed(minutes, job.getTruckOnTheJob())) {
            job.setJobStatus(JobStatus.CRASH);
            double spentFuel = minutes * job.getTruckOnTheJob().getTruckModel().getFuelConsumingPerformance();
            userProfile.setTotalMoney(userProfile.getTotalMoney() - spentFuel);
            jobRepository.save(job);
            userProfileRepository.save(userProfile);
            truck.setOnTheJob(false);
            truckRepository.save(truck);
            throw new TruckCrashedException();
        }
        double spentFuel = minutes * job.getTruckOnTheJob().getTruckModel().getFuelConsumingPerformance()/2.5;
        double earnedMoney = job.getCharge() - spentFuel;
        userProfile.setTotalMoney(userProfile.getTotalMoney() + earnedMoney);
        userProfileRepository.save(userProfile);
        jobRepository.delete(job);

        truck.setOnTheJob(false);
        truckRepository.save(truck);

        JobDto jobDto = new JobDto();
        jobDto.setSuccessMessage("Job successfully finished. Job's charge : " + job.getCharge() + " , " + "fuel consumption : " + spentFuel);
        jobDto.setJobCharge(job.getCharge());
        jobDto.setSpentFuel(spentFuel);
        jobDto.setEarnedMoney(earnedMoney);
        return jobDto;

    }

    private boolean isTruckCrashed(long minutes, Truck truck){

        double crashProbability = truck.getTruckModel().getCrashRisk() / 10.0;
        crashProbability += minutes / 1000.0;

        Random random = new Random();
        int randInt = random.nextInt(101);

        return randInt <= crashProbability;
    }

    private double calculateDistance(TakeJobDto takeJobDto, Job job) throws DifferentRegionDistanceCalculationException, InvalidRouteForJobException {
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
        return distance;
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
