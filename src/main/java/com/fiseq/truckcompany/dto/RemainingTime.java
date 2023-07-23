package com.fiseq.truckcompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiseq.truckcompany.entities.Job;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemainingTime {
    private long days;
    private long hours;
    private long minutes;
    private long seconds;

    public RemainingTime(Job job) {
        Duration duration = Duration.between(job.getCompletionTime(), LocalDateTime.now());
        this.days = duration.toDays();
        this.hours = duration.toHours() % 24;
        this.minutes = duration.toMinutes() % 60;
        this.seconds = duration.getSeconds() % 60;
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }
}
