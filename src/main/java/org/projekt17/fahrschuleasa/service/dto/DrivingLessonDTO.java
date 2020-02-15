package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.DrivingLesson;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;

import java.util.List;
import java.util.stream.Collectors;


public class DrivingLessonDTO extends LessonDTO {

    private SmallStudentDTO driver;

    private DrivingCategory drivingCategory;

    private List<SmallStudentDTO> missingStudents;

    private List<SmallStudentDTO> lateMissingStudents;

    private Location pickup;

    private Location destination;

    private DrivingLessonType lessonType;

    private Boolean bookable;

    private Long teacherId;

    public DrivingLessonDTO() {}

    public DrivingLessonDTO(DrivingLesson drivingLesson) {
        super(drivingLesson);
        if (drivingLesson.getTeacher() != null) {
            teacherId = drivingLesson.getTeacher().getId();
        }
        if (drivingLesson.getDriver() != null) {
            driver = new SmallStudentDTO(drivingLesson.getDriver());
            drivingCategory = drivingLesson.getDriver().getCategory();
        }
        missingStudents = drivingLesson.getMissingStudents().stream()
            .map(SmallStudentDTO::new).collect(Collectors.toList());
        lateMissingStudents = drivingLesson.getLateMissingStudents().stream()
            .map(SmallStudentDTO::new).collect(Collectors.toList());
        pickup = drivingLesson.getPickup();
        destination = drivingLesson.getDestination();
        lessonType = drivingLesson.getLessonType();
        bookable = drivingLesson.getBookable();
    }

    public SmallStudentDTO getDriver(){
        return this.driver;
    }

    public void setDriver(SmallStudentDTO smallStudentDTO){
        this.driver = smallStudentDTO;
    }

    public Long getDriverId() {
        return driver == null ? null : driver.getStudentId();
    }

    public DrivingCategory getDrivingCategory() {
        return drivingCategory;
    }

    public void setDrivingCategory(DrivingCategory drivingCategory) {
        this.drivingCategory = drivingCategory;
    }

    public List<SmallStudentDTO> getMissingStudents() {
        return missingStudents;
    }

    public void setMissingStudents(List<SmallStudentDTO> missingStudents) {
        this.missingStudents = missingStudents;
    }

    public List<SmallStudentDTO> getLateMissingStudents() {
        return lateMissingStudents;
    }

    public void setLateMissingStudents(List<SmallStudentDTO> lateMissingStudents) {
        this.lateMissingStudents = lateMissingStudents;
    }

    public DrivingLessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(DrivingLessonType drivingLessonType) {
        this.lessonType = drivingLessonType;
    }

    public Location getPickup() {
        return pickup;
    }

    public void setPickup(Location pickup) {
        this.pickup = pickup;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Boolean getBookable() {
        return bookable;
    }

    public void setBookable(Boolean bookable) {
        this.bookable = bookable;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "DrivingLessonDTO{" +
            "id=" + super.getId() +
            ", begin=" + super.getBegin() +
            ", end=" + super.getEnd() +
            ", driver=" + this.driver +
            ", drivingCategory=" + drivingCategory +
            ", teacherId=" + teacherId +
            ", pickup=" + pickup +
            ", destination=" + destination +
            ", lessonType=" + lessonType +
            ", bookable=" + bookable +
            '}';
    }
}
