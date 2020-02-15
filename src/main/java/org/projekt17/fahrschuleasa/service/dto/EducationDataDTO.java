package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;

public class EducationDataDTO {

    private Long studentId;

    private Integer basic;

    private Integer advanced;

    private Integer performance;

    private Integer independence;

    private Integer overland;

    private Integer autobahn;

    private Integer night;

    private Integer basicCount;

    private Integer overlandCount;

    private Integer autobahnCount;

    private Integer nightCount;

    private Integer drivingLessonsCount;

    private Integer missedDrivingLessonsCount;

    private Integer lateMissedDrivingLessonsCount;

    private Integer theoryLessonsCount;

    private DrivingCategory drivingCategory;

    private Boolean readyForTheory;

    private Boolean missionAccomplished;

    private Integer wantedLessons;

    private Integer allowedLessons;

    private Long teacherId;

    public EducationDataDTO() {}

    public EducationDataDTO(Student student) {
        studentId = student.getId();
        if (student.getTeachingDiagram() != null) {
            TeachingDiagram teachingDiagram = student.getTeachingDiagram();
            basic = teachingDiagram.getBasic();
            advanced = teachingDiagram.getAdvanced();
            performance = teachingDiagram.getPerformance();
            independence = teachingDiagram.getIndependence();
            overland = teachingDiagram.getOverland();
            autobahn = teachingDiagram.getAutobahn();
            night = teachingDiagram.getNight();
        }
        basicCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.NORMAL)).count();
        overlandCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.OVERLAND)).count();
        autobahnCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.AUTOBAHN)).count();
        nightCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.NIGHT)).count();

        drivingLessonsCount = student.getDrivingLessons().size();
        missedDrivingLessonsCount = student.getMissedLessons().size();
        lateMissedDrivingLessonsCount = student.getLateMissedLessons().size();
        theoryLessonsCount = student.getTheoryLessons().size();

        drivingCategory = student.getCategory();
        readyForTheory = student.isReadyForTheory();
        missionAccomplished = student.getMissionAccomplished();
        wantedLessons = student.getWantedLessons();
        allowedLessons = student.getAllowedLessons();
        if (student.getTeacher() != null) {
            teacherId = student.getTeacher().getId();
        }
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getBasic() {
        return basic;
    }

    public void setBasic(Integer basic) {
        this.basic = basic;
    }

    public Integer getAdvanced() {
        return advanced;
    }

    public void setAdvanced(Integer advanced) {
        this.advanced = advanced;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Integer getIndependence() {
        return independence;
    }

    public void setIndependence(Integer independence) {
        this.independence = independence;
    }

    public Integer getOverland() {
        return overland;
    }

    public void setOverland(Integer overland) {
        this.overland = overland;
    }

    public Integer getAutobahn() {
        return autobahn;
    }

    public void setAutobahn(Integer autobahn) {
        this.autobahn = autobahn;
    }

    public Integer getNight() {
        return night;
    }

    public void setNight(Integer night) {
        this.night = night;
    }

    public Integer getBasicCount() {
        return basicCount;
    }

    public void setBasicCount(Integer basicCount) {
        this.basicCount = basicCount;
    }

    public Integer getOverlandCount() {
        return overlandCount;
    }

    public void setOverlandCount(Integer overlandCount) {
        this.overlandCount = overlandCount;
    }

    public Integer getAutobahnCount() {
        return autobahnCount;
    }

    public void setAutobahnCount(Integer autobahnCount) {
        this.autobahnCount = autobahnCount;
    }

    public Integer getNightCount() {
        return nightCount;
    }

    public void setNightCount(Integer nightCount) {
        this.nightCount = nightCount;
    }

    public Integer getDrivingLessonsCount() {
        return drivingLessonsCount;
    }

    public void setDrivingLessonsCount(Integer drivingLessonsCount) {
        this.drivingLessonsCount = drivingLessonsCount;
    }

    public Integer getMissedDrivingLessonsCount() {
        return missedDrivingLessonsCount;
    }

    public void setMissedDrivingLessonsCount(Integer missedDrivingLessonsCount) {
        this.missedDrivingLessonsCount = missedDrivingLessonsCount;
    }

    public Integer getLateMissedDrivingLessonsCount() {
        return lateMissedDrivingLessonsCount;
    }

    public void setLateMissedDrivingLessonsCount(Integer lateMissedDrivingLessonsCount) {
        this.lateMissedDrivingLessonsCount = lateMissedDrivingLessonsCount;
    }

    public Integer getTheoryLessonsCount() {
        return theoryLessonsCount;
    }

    public void setTheoryLessonsCount(Integer theoryLessonsCount) {
        this.theoryLessonsCount = theoryLessonsCount;
    }

    public DrivingCategory getDrivingCategory() {
        return drivingCategory;
    }

    public void setDrivingCategory(DrivingCategory drivingCategory) {
        this.drivingCategory = drivingCategory;
    }

    public Boolean getReadyForTheory() {
        return readyForTheory;
    }

    public void setReadyForTheory(Boolean readyForTheory) {
        this.readyForTheory = readyForTheory;
    }

    public Boolean getMissionAccomplished() {
        return missionAccomplished;
    }

    public void setMissionAccomplished(Boolean missionAccomplished) {
        this.missionAccomplished = missionAccomplished;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "EducationDataDTO{" +
            "studentId=" + studentId +
            ", basic=" + basic +
            ", advanced=" + advanced +
            ", performance=" + performance +
            ", independence=" + independence +
            ", overland=" + overland +
            ", autobahn=" + autobahn +
            ", night=" + night +
            ", basicCount=" + basicCount +
            ", overlandCount=" + overlandCount +
            ", autobahnCount=" + autobahnCount +
            ", nightCount=" + nightCount +
            ", drivingLessonsCount=" + drivingLessonsCount +
            ", missedDrivingLessonsCount=" + missedDrivingLessonsCount +
            ", lateMissedDrivingLessonsCount=" + lateMissedDrivingLessonsCount +
            ", theoryLessonsCount=" + theoryLessonsCount +
            ", drivingCategory=" + drivingCategory +
            ", readyForTheory=" + readyForTheory +
            ", missionAccomplished=" + missionAccomplished +
            ", wantedLessons=" + wantedLessons +
            ", allowedLessons=" + allowedLessons +
            ", teacherId=" + teacherId +
            '}';
    }
}
