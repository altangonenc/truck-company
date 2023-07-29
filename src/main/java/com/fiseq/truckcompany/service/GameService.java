package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.TruckModel;
import com.fiseq.truckcompany.dto.JobDto;
import com.fiseq.truckcompany.dto.TakeJobDto;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.exception.*;

import java.util.List;

public interface GameService {
    List<TruckModel> getAllTruckModels(String token) throws InvalidAuthException;

    TruckModel getTruckAttributes(String token, String truckModel) throws InvalidAuthException, IllegalArgumentException;

    TruckDto buyTruck(String token, String truckName) throws Exception;

    JobDto getAllJobsInTerminal(String token, String terminalName) throws InvalidAuthException;

    JobDto takeJob(String token, TakeJobDto takeJobDto, Long jobId) throws InvalidAuthException, DifferentRegionDistanceCalculationException, InvalidRouteForJobException;

    JobDto finishJob(String token, Long jobId) throws InvalidAuthException, JobIsNotFinishedException, TruckCrashedException;

    JobDto getAllJobs(String token) throws InvalidAuthException;

    JobDto getAllJobsForUser(String token) throws InvalidAuthException;
}
