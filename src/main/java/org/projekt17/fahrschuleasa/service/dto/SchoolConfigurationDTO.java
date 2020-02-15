package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SchoolConfigurationDTO {

    private int initialLessons;

    private int planningWeekday;

    private int planningHour;

    private int planningMinute;

    private int maxInactive;

    private int deadlineMissedLesson;

    private List<String> availableCategories;

    private List<String> allCategories;

    private String emailSignature;

    public SchoolConfigurationDTO() {
        initialLessons = SchoolConfiguration.getInitialLessons();
        planningWeekday = SchoolConfiguration.getCronExpression().getWeekday();
        planningWeekday = planningWeekday == 7 ? 0 : planningWeekday;
        planningHour = SchoolConfiguration.getCronExpression().getHour();
        planningMinute = SchoolConfiguration.getCronExpression().getMinute();
        maxInactive = SchoolConfiguration.getMaxInactive();
        deadlineMissedLesson = SchoolConfiguration.getDeadlineMissedLesson();
        availableCategories = SchoolConfiguration.getAvailableCategories().stream().map(Enum::toString).collect(Collectors.toList());
        allCategories = Arrays.stream(DrivingCategory.values()).map(Enum::toString).collect(Collectors.toList());
        emailSignature = SchoolConfiguration.getEmailSignature();
    }

    public int getInitialLessons() {
        return initialLessons;
    }

    public void setInitialLessons(int initialLessons) {
        this.initialLessons = initialLessons;
    }

    public int getPlanningWeekday() {
        return planningWeekday;
    }

    public void setPlanningWeekday(int planningWeekday) {
        this.planningWeekday = planningWeekday;
    }

    public int getPlanningHour() {
        return planningHour;
    }

    public void setPlanningHour(int planningHour) {
        this.planningHour = planningHour;
    }

    public int getPlanningMinute() {
        return planningMinute;
    }

    public void setPlanningMinute(int planningMinute) {
        this.planningMinute = planningMinute;
    }

    public int getMaxInactive() {
        return maxInactive;
    }

    public void setMaxInactive(int maxInactive) {
        this.maxInactive = maxInactive;
    }

    public int getDeadlineMissedLesson() {
        return deadlineMissedLesson;
    }

    public void setDeadlineMissedLesson(int deadlineMissedLesson) {
        this.deadlineMissedLesson = deadlineMissedLesson;
    }

    public List<String> getAvailableCategories() {
        return availableCategories;
    }

    public void setAvailableCategories(List<String> availableCategories) {
        this.availableCategories = availableCategories;
    }

    public List<String> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<String> allCategories) {
        this.allCategories = allCategories;
    }

    public String getEmailSignature() {
        return emailSignature;
    }

    public void setEmailSignature(String emailSignature) {
        this.emailSignature = emailSignature;
    }

    @Override
    public String toString() {
        return "SchoolConfigurationDTO{" +
            "initialLessons=" + initialLessons +
            ", planningWeekday=" + planningWeekday +
            ", planningHour=" + planningHour +
            ", planningMinute=" + planningMinute +
            ", maxInactive=" + maxInactive +
            ", deadlineMissedLesson=" + deadlineMissedLesson +
            ", availableCategories=" + availableCategories +
            ", allCategories=" + allCategories +
            ", emailSignature='" + emailSignature + '\'' +
            '}';
    }
}
