package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeSlotDTO {

    private Long id;

    private Integer begin;

    private Integer end;

    private DayOfWeek day;

    private Long teacherId;

    private Set<DrivingCategory> preferredCategories;

    private Set<DrivingCategory> optionalCategories;

    private List<LocalDate> blockedDates;

    public TimeSlotDTO() {
    }

    public TimeSlotDTO(TimeSlot timeSlot) {
        id = timeSlot.getId();
        begin = timeSlot.getBegin();
        end = timeSlot.getEnd();
        day = timeSlot.getDay();
        if (timeSlot.getTeacher() != null)
            teacherId = timeSlot.getTeacher().getId();

        preferredCategories = timeSlot.getPreferredCategories();
        optionalCategories = timeSlot.getOptionalCategories();
        LocalDate now = LocalDate.now().minusDays(1);
        blockedDates = timeSlot.getBlockedDates().stream().filter(now::isBefore).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBegin() {
        return this.begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return this.end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public DayOfWeek getDay() {
        return this.day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Set<DrivingCategory> getPreferredCategories() {
        return this.preferredCategories;
    }

    public void setPreferredCategories(Set<DrivingCategory> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public Set<DrivingCategory> getOptionalCategories() {
        return this.optionalCategories;
    }

    public void setOptionalCategories(Set<DrivingCategory> optionalCategories) {
        this.optionalCategories = optionalCategories;
    }

    public List<LocalDate> getBlockedDates() {
        return this.blockedDates;
    }

    public void setBlockedDates(List<LocalDate> blockedDates) {
        this.blockedDates = blockedDates;
    }

    @Override
    public String toString() {
        return "TimeSlotDTO{" +
            "id=" + id +
            ", begin=" + begin +
            ", end=" + end +
            ", day=" + day +
            ", teacherId=" + teacherId +
            ", preferredCategories=" + preferredCategories +
            ", optionalCategories=" + optionalCategories +
            ", blockedDates=" + blockedDates +
            '}';
    }
}
