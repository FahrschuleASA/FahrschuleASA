package org.projekt17.fahrschuleasa.scheduler;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ScheduledTasks {

    private final TaskScheduler taskScheduler;

    private final PlannerTask plannerTask;

    public ScheduledTasks(TaskScheduler taskScheduler, PlannerTask plannerTask) {
        this.taskScheduler = taskScheduler;
        this.plannerTask = plannerTask;
    }

    @PostConstruct
    public void schedule() {
        taskScheduler.schedule(plannerTask, new ResetableCronTrigger(SchoolConfiguration.getCronExpression().getCronExpression()));
    }
}
