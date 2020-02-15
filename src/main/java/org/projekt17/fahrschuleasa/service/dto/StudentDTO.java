package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

public class StudentDTO extends MyAccountDTO {

    private DrivingCategory category;

    private Boolean readyForTheory;

    private Boolean notifyForFreeLesson;

    private Integer wantedLessons;

    private Integer allowedLessons;

    private Integer lateCanceledLessons;

    private Integer canceledLessons;

    private Boolean changedPreferences;

    private Long teacherId;

    public StudentDTO(){}

    public StudentDTO(Student student) {
        super(student);

        this.category = student.getCategory();
        this.readyForTheory = student.isReadyForTheory();
        this.notifyForFreeLesson = student.getNotifyForFreeLesson();
        this.wantedLessons = student.getWantedLessons();
        this.allowedLessons = student.getAllowedLessons();
        this.lateCanceledLessons = student.getLateMissedLessons().size();
        this.canceledLessons = student.getMissedLessons().size();
        this.changedPreferences = student.isChangedPreferences();
        if (student.getTeacher() != null)
            this.teacherId = student.getTeacher().getId();
    }

    public DrivingCategory getCategory() {
        return category;
    }

    public void setCategory(DrivingCategory category) {
        this.category = category;
    }

    public Boolean getReadyForTheory() {
        return readyForTheory;
    }

    public void setReadyForTheory(Boolean readyForTheory) {
        this.readyForTheory = readyForTheory;
    }

    public Boolean getNotifyForFreeLesson() {
        return notifyForFreeLesson;
    }

    public void setNotifyForFreeLesson(Boolean notifyForFreeLesson) {
        this.notifyForFreeLesson = notifyForFreeLesson;
    }

    public Integer getWantedLessons() {
        return wantedLessons;
    }

    public void setWantedLessons(Integer wantedLessons) {
        this.wantedLessons = wantedLessons;
    }

    public Integer getAllowedLessons() {
        return allowedLessons;
    }

    public void setAllowedLessons(Integer allowedLessons) {
        this.allowedLessons = allowedLessons;
    }

    public Integer getLateCanceledLessons() {
        return lateCanceledLessons;
    }

    public void setLateCanceledLessons(Integer lateCanceledLessons) {
        this.lateCanceledLessons = lateCanceledLessons;
    }

    public Integer getCanceledLessons() {
        return canceledLessons;
    }

    public void setCanceledLessons(Integer canceledLessons) {
        this.canceledLessons = canceledLessons;
    }

    public Boolean getChangedPreferences() {
        return changedPreferences;
    }

    public void setChangedPreferences(Boolean changedPreferences) {
        this.changedPreferences = changedPreferences;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
            "category=" + category +
            ", readyForTheory=" + readyForTheory +
            ", wantedLessons=" + wantedLessons +
            ", allowedLessons=" + allowedLessons +
            ", lateCanceledLessons=" + lateCanceledLessons +
            ", changedPreferences=" + changedPreferences +
            ", teacherId=" + teacherId +
            ", with: " + super.toString() +
            '}';
    }
}
