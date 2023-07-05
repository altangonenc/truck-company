package com.fiseq.truckcompany.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatus {
    SUCCESS("Succeed"),
    CRASH("Crashed"),
    IN_PROGRESS("In progress"),
    VACANT("On hold");
    private String name;
}
