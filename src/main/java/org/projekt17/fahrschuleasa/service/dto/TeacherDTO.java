package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Teacher;

public class TeacherDTO extends MyAccountDTO {

    private Boolean changedTimeSlots;

    private Boolean schoolOwner;

    public TeacherDTO(){}

    public TeacherDTO(Teacher teacher){
        super(teacher);
        changedTimeSlots = teacher.isChangedTimeSlots();
        schoolOwner = teacher.isSchoolOwner();
    }

    public Boolean getChangedTimeSlots() {
        return changedTimeSlots;
    }

    public void setChangedTimeSlots(Boolean changedTimeSlots) {
        this.changedTimeSlots = changedTimeSlots;
    }

    public Boolean getSchoolOwner() {
        return schoolOwner;
    }

    public void setSchoolOwner(Boolean schoolOwner) {
        this.schoolOwner = schoolOwner;
    }

    @Override
    public String toString() {
        return "TeacherDTO{" +
            "changedTimeSlots=" + changedTimeSlots +
            ", schoolOwner=" + schoolOwner +
            ", with: " + super.toString() +
            '}';
    }
}
