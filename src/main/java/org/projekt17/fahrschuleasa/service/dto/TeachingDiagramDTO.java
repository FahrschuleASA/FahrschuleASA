package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;

public class TeachingDiagramDTO {

    private Long id;

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

    public TeachingDiagramDTO() {}

    public TeachingDiagramDTO(Student student) {
        TeachingDiagram teachingDiagram = student.getTeachingDiagram();
        id = teachingDiagram.getId();
        basic = teachingDiagram.getBasic();
        advanced = teachingDiagram.getAdvanced();
        performance = teachingDiagram.getPerformance();
        independence = teachingDiagram.getIndependence();
        overland = teachingDiagram.getOverland();
        autobahn = teachingDiagram.getAutobahn();
        night = teachingDiagram.getNight();
        basicCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.NORMAL)).count();
        overlandCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.OVERLAND)).count();
        autobahnCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.AUTOBAHN)).count();
        nightCount = (int) student.getDrivingLessons().stream()
            .filter(drivingLesson -> drivingLesson.getLessonType().equals(DrivingLessonType.NIGHT)).count();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "TeachingDiagramDTO{" +
            "id=" + id +
            ", basic=" + basic +
            ", advanced=" + advanced +
            ", performance=" + performance +
            ", independence=" + independence +
            ", overland=" + overland +
            ", autobahn=" + autobahn +
            ", night=" + night +
            '}';
    }
}
