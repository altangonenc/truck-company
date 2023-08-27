package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.*;
import com.fiseq.truckcompany.dto.*;
import com.fiseq.truckcompany.entities.*;
import com.fiseq.truckcompany.exception.*;
import com.fiseq.truckcompany.repository.*;
import com.fiseq.truckcompany.utilities.DifferentRegionDistanceCalculator;
import com.fiseq.truckcompany.utilities.SameRegionDistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GameServiceImpl implements GameService{
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TruckRepository truckRepository;
    private final JobRepository jobRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public GameServiceImpl(UserService userService, UserRepository userRepository, UserProfileRepository userProfileRepository, TruckRepository truckRepository, JobRepository jobRepository, ItemRepository itemRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.truckRepository = truckRepository;
        this.jobRepository = jobRepository;
        this.itemRepository = itemRepository;
    }

    public ResponseEntity<TruckDto> getAllTruckModels(String token) {
        try {
            checkToken(token);
            ArrayList<TruckModel> truckModels = new ArrayList<>(Arrays.asList(TruckModel.values()));
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

    public ResponseEntity<TruckDto> getTruckAttributes(String token, String truckModelName) {
        try {
            checkToken(token);
            TruckModel truckModel = TruckModel.valueOf(truckModelName);
            TruckDto truckDto = new TruckDto();
            truckDto.setTruckModel(truckModel);
            HashMap<String,Object> truckAttributes = new HashMap<>();
            truckAttributes.put("model",truckModel.getModel());
            truckAttributes.put("brand",truckModel.getBrand());
            truckAttributes.put("crashRisk",truckModel.getCrashRisk());
            truckAttributes.put("id",truckModel.getTruckModelId());
            truckAttributes.put("fuelPerformance",truckModel.getFuelConsumingPerformance());
            truckAttributes.put("speed",truckModel.getSpeedPerformance());
            truckAttributes.put("price",truckModel.getPrice());
            truckAttributes.put("maxMileageOfTruck", truckModel.getMaxMileageOfTruck());
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

    public ResponseEntity<TruckDto> buyTruck(String token, String truckName, String location) {
        try {
            UserProfile userProfile = extractUserProfileFromToken(token);
            FreightTerminals terminal = checkTerminalNameAndReturn(location);
            TruckModel truckModel = TruckModel.valueOf(truckName);

            if (checkUsersMoneyAndCompareItWithPriceOfTruck(truckModel.getPrice(), userProfile)) {
                double moneyLeft = userProfile.getTotalMoney() - truckModel.getPrice();
                userProfile.setTotalMoney(moneyLeft);
                userProfileRepository.save(userProfile);

                Truck truck = new Truck();
                truck.setTruckModel(truckModel);
                truck.setOwner(userProfile);
                truck.setUnavailable(false);
                truck.setLocation(terminal);
                truck.setMaxMileageOfTruck(truckModel.getMaxMileageOfTruck());
                truckRepository.save(truck);

                TruckDto truckDto = new TruckDto();
                truckDto.setTruckModel(truckModel);
                truckDto.setMoneyLeftInAccount(moneyLeft);
                return new ResponseEntity<>(truckDto, HttpStatus.OK);
            }
            throw new NotEnoughMoneyException(HttpStatus.BAD_REQUEST, GameErrorMessages.USERS_MONEY_IS_NOT_ENOUGH_TO_BUY_THIS_TRUCK);

        } catch (InvalidAuthException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(truckDto, e.getHttpStatus());
        } catch (IllegalArgumentException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(GameErrorMessages.GIVEN_TRUCK_MODEL_OR_TERMINAL_NOT_FOUND.getUserText());
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

    public ResponseEntity<JobDto> getAllJobsInTerminal(String token, String terminalName) {
        try {
            checkToken(token);
            FreightTerminals terminal = checkTerminalNameAndReturn(terminalName);
            JobDto jobDto = new JobDto();
            jobDto.setAllJobsInTerminal(jobRepository.findAllByOriginationTerminal(terminal));
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

    public ResponseEntity<JobDto> getAllJobs(String token) {
        try {
            checkToken(token);
            JobDto jobDto = new JobDto();
            List<Job> jobs = jobRepository.findAllByJobStatusEquals(JobStatus.VACANT).orElseThrow();
            jobDto.setAllJobs(jobs);
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

    public ResponseEntity<JobDto> getAllJobsForUser(String token) {
        try {
            UserProfile userProfile = extractUserProfileFromToken(token);
            JobDto jobDto = new JobDto();
            List<Job> jobs = jobRepository.findAllByJobStatusEqualsOrJobStatusEqualsOrJobStatusEqualsAndOwnerEquals(JobStatus.IN_PROGRESS,
                    JobStatus.CRASH,
                    JobStatus.SUCCESS,
                    userProfile).orElseThrow();
            jobDto.setAllJobs(jobs);
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (InvalidAuthException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(GameErrorMessages.THERE_IS_NO_JOB_FOR_USER.getUserText());
            return new ResponseEntity<>(jobDto, HttpStatus.OK);
        } catch (Exception e) {
            JobDto jobDto = new JobDto();
            jobDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(jobDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAllTrucksOfUser(String token) {
        try {
            UserProfile userProfile = extractUserProfileFromToken(token);
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
                attributes.put("maxMileageOfTruck", truck.getMaxMileageOfTruck());
                truckDto.setTruckModelAttributes(attributes);
                truckDto.setTruckId(truck.getId());
                truckDto.setOnTheJob(truck.isUnavailable());

                allTheTruckDtos.add(truckDto);
            }
            return new ResponseEntity<>(allTheTruckDtos, HttpStatus.OK);

        }  catch (InvalidAuthException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(truckDto, e.getHttpStatus());
        } catch (NoSuchElementException e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(GameErrorMessages.USER_HAS_NO_TRUCK.getUserText());
            return new ResponseEntity<>(truckDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TruckDto truckDto = new TruckDto();
            truckDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(truckDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<JobDto> takeJob(String token, TakeJobDto takeJobDto, Long jobId) {
        try {
            UserProfile userProfile = extractUserProfileFromToken(token);
            Job job = jobRepository.findByIdAndJobStatusEquals(jobId, JobStatus.VACANT).orElseThrow();

            Truck truck = truckRepository.findByIsUnavailableAndId(false, takeJobDto.getTruckId()).orElseThrow();
            double speedOfTruck = truck.getTruckModel().getSpeedPerformance();

            double distance = calculateDistance(takeJobDto, job, truck);

            isTruckOutdated(truck, distance);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime approximateCompletionOfJobTime = now.plusHours((long) distance / (long) speedOfTruck);

            truck.setUnavailable(true);
            truck.setMaxMileageOfTruck(truck.getMaxMileageOfTruck() - distance);
            truckRepository.save(truck);

            job.setCompletionTime(approximateCompletionOfJobTime);
            job.setStartedTime(now);
            job.setTruckOnTheJob(truck);
            job.setOwner(userProfile);
            job.setJobStatus(JobStatus.IN_PROGRESS);
            jobRepository.save(job);

            JobDto jobDto = new JobDto();
            jobDto.setSuccessMessage(GameSuccessMessages.SUCCESSFULLY_TAKE_JOB.getUserText());
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

    private void isTruckOutdated(Truck truck, double distance) {
        if (distance > truck.getMaxMileageOfTruck()) {
            throw new OutdatedTruckException();
        }
    }

    public ResponseEntity<JobDto> finishJob(String token, Long jobId) {
        try {
            UserProfile userProfile = extractUserProfileFromToken(token);
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
                truck.setUnavailable(false);
                truckRepository.save(truck);
                throw new TruckCrashedException();
            }
            double spentFuel = minutes * job.getTruckOnTheJob().getTruckModel().getFuelConsumingPerformance()/2.5;
            double earnedMoney = job.getCharge() - spentFuel;
            userProfile.setTotalMoney(userProfile.getTotalMoney() + earnedMoney);
            userProfileRepository.save(userProfile);
            job.setJobStatus(JobStatus.SUCCESS);
            jobRepository.save(job);

            truck.setUnavailable(false);
            truck.setLocation(job.getDestinationTerminal());
            truckRepository.save(truck);

            JobDto jobDto = new JobDto();
            jobDto.setSuccessMessage("Job successfully finished. Job's charge : " + job.getCharge() + " , " + "fuel consumption : " + spentFuel);
            jobDto.setJobCharge(job.getCharge());
            jobDto.setSpentFuel(spentFuel);
            jobDto.setEarnedMoney(earnedMoney);
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

    private boolean isTruckCrashed(long minutes, Truck truck){

        double crashProbability = truck.getTruckModel().getCrashRisk() / 10.0;
        crashProbability += minutes / 1000.0;

        Random random = new Random();
        int randInt = random.nextInt(101);

        return randInt <= crashProbability;
    }

    private double calculateTrucksLocationToStartTerminal(Job job, Truck truck) {
        double distance = 0;
        if (job.getOriginationTerminal() == truck.getLocation()) {
            return 0;
        }
        if (job.getOriginationTerminal().getRegion() != truck.getLocation().getRegion()) {
            DifferentRegionDistanceCalculator calculator = new DifferentRegionDistanceCalculator(job.getOriginationTerminal(),job.getDestinationTerminal());
            distance += calculator.calculateRoute();
        }
        if (job.getOriginationTerminal().getRegion() == job.getDestinationTerminal().getRegion()) {
            SameRegionDistanceCalculator calculator = new SameRegionDistanceCalculator(job.getOriginationTerminal(),job.getDestinationTerminal());
            distance += calculator.calculateRoute();
        }
        truck.setLocation(job.getOriginationTerminal());
        return distance;
    }

    private double calculateDistance(TakeJobDto takeJobDto, Job job, Truck truck) throws DifferentRegionDistanceCalculationException, InvalidRouteForJobException {
        double distance = calculateTrucksLocationToStartTerminal(job, truck);

        if (takeJobDto.getRoute() == null) {
            if (job.getOriginationTerminal().getRegion() != job.getDestinationTerminal().getRegion()) {
                DifferentRegionDistanceCalculator calculator = new DifferentRegionDistanceCalculator(job.getOriginationTerminal(),job.getDestinationTerminal());
                distance += calculator.calculateRoute();
            }
            if (job.getOriginationTerminal().getRegion() == job.getDestinationTerminal().getRegion()) {
                SameRegionDistanceCalculator calculator = new SameRegionDistanceCalculator(job.getOriginationTerminal(),job.getDestinationTerminal());
                distance += calculator.calculateRoute();
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
                    distance += distance + calculator.calculateRoute();
                }
                if (from.getRegion() == to.getRegion()) {
                    SameRegionDistanceCalculator calculator = new SameRegionDistanceCalculator(from,to);
                    distance += distance + calculator.calculateRoute();
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

    private UserProfile extractUserProfileFromToken(String token) {
        String username = checkTokenAndReturnUsername(token);
        User user = userRepository.findByUserName(username);
        return user.getUserProfile();
    }

    public ResponseEntity<ItemSellDto> sellItem(String token, ItemSellRequestDto itemSellRequestDto) {
        try {
            UserProfile userProfile = extractUserProfileFromToken(token);
            validatePriceOfItem(itemSellRequestDto.getPrice());

            Truck truck = getTruckWhichIsNotUnavailable(userProfile, itemSellRequestDto);
            truck.setUnavailable(true);

            Item item = new Item();
            item.setPrice(itemSellRequestDto.getPrice());
            item.setTruck(truck);

            truckRepository.save(truck);
            itemRepository.save(item);

            ItemSellDto itemSellDto = new ItemSellDto();
            itemSellDto.setSuccessMessage(GameSuccessMessages.ITEM_SUCCESSFULLY_PLACED_IN_MARKETPLACE.getUserText());
            itemSellDto.setTruckId(truck.getId());
            itemSellDto.setItemId(item.getId());
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

    private Truck getTruckWhichIsNotUnavailable(UserProfile userProfile, ItemSellRequestDto itemSellRequestDto) {
        return truckRepository.findByOwnerAndIdAndIsUnavailable(userProfile, itemSellRequestDto.getTruckId(), false).orElseThrow();
    }

    private void validatePriceOfItem(double price) {
        if (price > GameConstants.UPPER_LIMIT_PRICE || price < GameConstants.LOWER_LIMIT_PRICE) {
            throw new IncorrectPricingException();
        }
    }

    public ResponseEntity<MarketplaceDto> getAllItemsInMarketplace(String authorizationHeader, Double minPrice, Double maxPrice, String truckName) {
        try {
            checkToken(authorizationHeader);
            ArrayList<TruckItemDto> truckItemDtos = getFilteredItems(minPrice, maxPrice, truckName);
            MarketplaceDto marketplaceDto = new MarketplaceDto();
            marketplaceDto.setItems(truckItemDtos);
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

    private ArrayList<TruckItemDto> getFilteredItems(Double minPrice, Double maxPrice, String truckName) {
        List<Item> listOfTruckItems = getAllItemsFilteredByTruckName(truckName);

        ArrayList<TruckItemDto> truckItemDtos = new ArrayList<>();

        for (Item item : listOfTruckItems) {
            if ((minPrice == null || item.getPrice() >= minPrice) &&
                    (maxPrice == null || item.getPrice() <= maxPrice)) {

                TruckItemDto truckItemDto = new TruckItemDto();
                truckItemDto.setTruckId(item.getId());
                truckItemDto.setTruckModel(item.getTruck().getTruckModel());
                truckItemDto.setPrice(item.getPrice());
                truckItemDtos.add(truckItemDto);
            }
        }
        return truckItemDtos;
    }

    private List<Item> getAllItemsFilteredByTruckName(String truckName) {
        List<Item> listOfTruckItems;
        if (truckName != null && !truckName.isEmpty()) {
            TruckModel truckModel = TruckModel.valueOf(truckName);
            listOfTruckItems = itemRepository.findByTruck_TruckModel(truckModel).orElseThrow();
        } else {
            listOfTruckItems = itemRepository.findAll();
        }
        return listOfTruckItems;
    }

    public ResponseEntity<ItemBuyDto> buyItem(String authorizationHeader, Long itemId) {
        try {
            String username = checkTokenAndReturnUsername(authorizationHeader);
            Item item = itemRepository.findById(itemId).orElseThrow();
            doPurchase(item, username);
            deleteItemFromMarketplace(item);
            ItemBuyDto itemBuyDto = new ItemBuyDto();
            itemBuyDto.setItemId(item.getId());
            itemBuyDto.setSuccessMessage(GameSuccessMessages.SUCCESSFULLY_BOUGHT_ITEM.getUserText());
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

    private void deleteItemFromMarketplace(Item item) {
        itemRepository.delete(item);
    }

    private void doPurchase(Item item, String username) {
        User user = userRepository.findByUserName(username);
        UserProfile userProfile = user.getUserProfile();

        checkTrucksOwnership(userProfile, item);
        doesUserHaveEnoughMoneyToBuy(item, userProfile);

        exchangeMoneyBetweenBuyerAndSeller(item, userProfile);
        exchangeItemBetweenBuyerAndSeller(item, userProfile);
    }

    private void checkTrucksOwnership(UserProfile userProfile, Item item) {
        if (userProfile == item.getTruck().getOwner()) {
            throw new CannotBuyTruckException();
        }
    }

    private void doesUserHaveEnoughMoneyToBuy(Item item, UserProfile userProfile) {
        if (item.getPrice() > userProfile.getTotalMoney()) {
            throw new NotEnoughMoneyException(HttpStatus.BAD_REQUEST, GameErrorMessages.USERS_MONEY_IS_NOT_ENOUGH_TO_BUY_THIS_ITEM);
        }
    }

    private void exchangeMoneyBetweenBuyerAndSeller(Item item, UserProfile buyer) {
        UserProfile seller = item.getTruck().getOwner();
        buyer.setTotalMoney(buyer.getTotalMoney() - item.getPrice());
        seller.setTotalMoney(seller.getTotalMoney() + item.getPrice());
        userProfileRepository.save(buyer);
        userProfileRepository.save(seller);
    }

    private void exchangeItemBetweenBuyerAndSeller(Item item, UserProfile buyer) {
        Truck truck = item.getTruck();
        truck.setOwner(buyer);
        truck.setUnavailable(false);
        truckRepository.save(truck);
    }
}
