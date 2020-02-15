package org.projekt17.fahrschuleasa.scheduler;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.config.util.CronExpression;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ResetableCronTrigger implements Trigger {
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Edited by Marvin Meiers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * {@link Trigger} implementation for cron expressions, that can be dynamically updated.
 * Wraps a {@link CronSequenceGenerator}.
 *
 * @author Juergen Hoeller
 * @author Marvin Meiers
 * @since 3.0
 * @see CronSequenceGenerator
 */

    private CronSequenceGenerator sequenceGenerator;

    private CronExpression currentExpression;

    private TimeZone timeZone = null;


    /**
     * Build a {@link org.springframework.scheduling.support.CronTrigger} from the pattern provided in the default time zone.
     * @param expression a space-separated list of time fields, following cron
     * expression conventions
     */
    public ResetableCronTrigger(String expression) {
        this.sequenceGenerator = new CronSequenceGenerator(expression);
        this.currentExpression = new CronExpression(expression);
    }

    /**
     * Build a {@link org.springframework.scheduling.support.CronTrigger} from the pattern provided in the given time zone.
     * @param expression a space-separated list of time fields, following cron
     * expression conventions
     * @param timeZone a time zone in which the trigger times will be generated
     */
    public ResetableCronTrigger(String expression, TimeZone timeZone) {
        this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
        this.currentExpression = new CronExpression(expression);
        this.timeZone = timeZone;
    }

    /**
     * Determine the next execution time according to the given trigger context.
     * <p>Next execution times are calculated based on the
     * {@linkplain TriggerContext#lastCompletionTime completion time} of the
     * previous execution; therefore, overlapping executions won't occur.
     * Moreover the most recent cron expression configured in {@link SchoolConfiguration}
     * is used. If the cron expression has changed and correlates to a time later in
     * the week than the current expression, 7 days are added to the calculated
     * execution time.
     */
    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            Date scheduled = triggerContext.lastScheduledExecutionTime();
            if (scheduled != null && date.before(scheduled)) {
                // Previous task apparently executed too early...
                // Let's simply use the last calculated execution time then,
                // in order to prevent accidental re-fires in the same second.
                date = scheduled;
            }
        }
        else {
            date = new Date();
        }

        Date nextRun;

        if (!this.currentExpression.equals(SchoolConfiguration.getCronExpression())) {
            if (timeZone == null)
                this.sequenceGenerator = new CronSequenceGenerator(SchoolConfiguration.getCronExpression().getCronExpression());
            else
                this.sequenceGenerator = new CronSequenceGenerator(SchoolConfiguration.getCronExpression().getCronExpression(), timeZone);
            nextRun = this.sequenceGenerator.next(date);

            if (SchoolConfiguration.getCronExpression().compareTo(currentExpression) > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(nextRun);

                calendar.add(Calendar.DAY_OF_MONTH, 7);

                nextRun = calendar.getTime();
            }
            currentExpression.setCronExpression(SchoolConfiguration.getCronExpression().getCronExpression());
        } else {
            nextRun = this.sequenceGenerator.next(date);
        }

        return nextRun;
    }

}
