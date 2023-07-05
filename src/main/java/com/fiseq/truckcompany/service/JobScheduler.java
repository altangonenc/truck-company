package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.constants.JobStatus;
import com.fiseq.truckcompany.entities.Job;
import com.fiseq.truckcompany.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class JobScheduler {
    private final JobRepository jobRepository;

    public JobScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    //works every hour
    @Scheduled(initialDelay = 0, fixedDelay = 3600000)
    public void checkAndCreateNewJobsIfNeeded() {
        for (FreightTerminals terminal : FreightTerminals.values()) {
            int currentAvailableJobCount = getCurrentJobCountForTerminal(terminal);
            int requiredJobCount = terminal.getCapacity() - currentAvailableJobCount;

            if (requiredJobCount > 0) {
                createNewJobsForTerminal(terminal, requiredJobCount);
            }
        }
    }

    private int getCurrentJobCountForTerminal(FreightTerminals terminal) {
        return jobRepository.countByOriginationTerminal(terminal);
    }

    private void createNewJobsForTerminal(FreightTerminals originatingTerminal, int requiredJobCount) {
        for (int i=0; i < requiredJobCount; i++) {
            Job newJob = new Job();
            newJob.setOriginationTerminal(originatingTerminal);

            FreightTerminals destinationTerminal = getRandomDestinationTerminal(originatingTerminal);
            newJob.setDestinationTerminal(destinationTerminal);
            newJob.setJobStatus(JobStatus.VACANT);

            double charge = getRandomCharge(newJob);
            newJob.setCharge(charge);

            jobRepository.save(newJob);
        }
    }

    private FreightTerminals getRandomDestinationTerminal(FreightTerminals originationTerminal) {
        FreightTerminals[] terminals = FreightTerminals.values();
        FreightTerminals destinationTerminal = terminals[new Random().nextInt(terminals.length)];

        while (destinationTerminal == originationTerminal) {
            destinationTerminal = terminals[new Random().nextInt(terminals.length)];
        }

        return destinationTerminal;
    }

    private double getRandomCharge(Job job) {
        if (job.getDestinationTerminal().getRegion() != job.getOriginationTerminal().getRegion()) {
            double minCharge = 150.0;
            double maxCharge = 200.0;
            return minCharge + (maxCharge - minCharge) * new Random().nextDouble();
        } else {
            double minCharge = 50.0;
            double maxCharge = 100.0;
            return minCharge + (maxCharge - minCharge) * new Random().nextDouble();
        }
    }
}
