package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.FreightTerminals;
import com.fiseq.truckcompany.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobSchedulerTest {
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    @Spy
    private JobScheduler jobScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkAndCreateNewJobsIfNeeded_NoRequiredJobs_ShouldNotCreateNewJobs() {
        int totalCapacity = 0;
        FreightTerminals[] terminals = FreightTerminals.values();

        for (FreightTerminals terminal : terminals) {
            totalCapacity += terminal.getCapacity();
        }

        when(jobRepository.countByOriginationTerminal(any())).thenReturn(totalCapacity);

        jobScheduler.checkAndCreateNewJobsIfNeeded();

        // Verify that no new jobs were created
        verify(jobRepository, never()).save(any());
    }

    @Test
    void checkAndCreateNewJobsIfNeeded_RequiredJobsExist_ShouldCreateNewJobs() {
        int totalCapacity = 0;
        FreightTerminals[] terminals = FreightTerminals.values();

        for (FreightTerminals terminal : terminals) {
            totalCapacity += terminal.getCapacity();
        }
        when(jobRepository.countByOriginationTerminal(any())).thenReturn(0);

        jobScheduler.checkAndCreateNewJobsIfNeeded();

        verify(jobRepository, times(totalCapacity)).save(any());
    }
}
