package org.projekt17.fahrschuleasa.config.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Objects;

public class CronExpression implements Comparable<CronExpression> {

    private Logger logger = LoggerFactory.getLogger(CronExpression.class);

    private int weekday = 7;
    private int hour = 22;
    private int minute = 0;

    private String cronExpression = "0 0 22 ? * 7";

    public CronExpression(int weekday, int hour, int minute) {
        setCronExpression(weekday, hour, minute);
    }

    public CronExpression(String cronExpression) {
        setCronExpression(cronExpression);
    }

    public CronExpression() {}

    public void setCronExpression(int weekday, int hour, int minute) {
        String cronExpression = String.format("0 %d %d ? * %d", minute, hour, weekday);
        if (!CronSequenceGenerator.isValidExpression(cronExpression)) {
            logger.error(String.format("Cron expression %s invalid! Fallback to %s.", cronExpression, this.cronExpression));
            return;
        }
        this.weekday = weekday == 0 ? 7 : weekday;
        this.hour = hour;
        this.minute = minute;
        this.cronExpression = String.format("0 %d %d ? * %d", this.minute, this.hour, this.weekday);
    }

    public void setCronExpression(String cronExpression) {
        if (!CronSequenceGenerator.isValidExpression(cronExpression)) {
            logger.error(String.format("Cron expression %s invalid! Fallback to %s.", cronExpression, this.cronExpression));
            return;
        }
        String[] fields = cronExpression.split(" ");
        if (Integer.parseInt(fields[0]) != 0 || !(fields[3].equals("?") || fields[3].equals("*")) || !fields[4].equals("*")) {
            logger.error(String.format("Cron expression %s invalid! Fallback to %s.", cronExpression, this.cronExpression));
            return;
        }
        this.weekday = Integer.parseInt(fields[5]);
        this.weekday = this.weekday == 0 ? 7 : this.weekday;
        this.hour = Integer.parseInt(fields[2]);
        this.minute = Integer.parseInt(fields[1]);
        this.cronExpression = String.format("0 %d %d ? * %d", this.minute, this.hour, this.weekday);
    }

    public int getWeekday() {
        return weekday;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CronExpression that = (CronExpression) o;
        return cronExpression.equals(that.cronExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cronExpression);
    }

    @Override
    public int compareTo(CronExpression o) {
        if (this.weekday < o.weekday)
            return -1;
        if (this.weekday > o.weekday)
            return 1;
        if (this.hour < o.hour)
            return -1;
        if (this.hour > o.hour)
            return 1;
        return Integer.compare(this.minute, o.minute);
    }
}
