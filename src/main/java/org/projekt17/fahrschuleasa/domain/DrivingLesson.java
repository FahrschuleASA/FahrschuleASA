package org.projekt17.fahrschuleasa.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;
import org.projekt17.fahrschuleasa.service.dto.DrivingLessonDTO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A DrivingLesson.
 */
@Entity
@Table(name = "driving_lesson")
public class DrivingLesson extends Lesson {

    @Column(name = "manual_lesson")
    private Boolean manualLesson;

    @Column(name = "bookable")
    private Boolean bookable;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    private DrivingLessonType lessonType = DrivingLessonType.NORMAL;

    @ManyToOne
    @JsonIgnoreProperties("drivingLessons")
    private Student driver;

    @ManyToMany(mappedBy = "missedLessons")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Student> missingStudents = new HashSet<>();

    @ManyToMany(mappedBy = "lateMissedLessons")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Student> lateMissingStudents = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("drivingLessons")
    private Teacher teacher;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("drivingLessons")
    private Location pickup;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("drivingLessons")
    private Location destination;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Student> optionalStudents = new HashSet<>();

    public DrivingLesson() {}

    public DrivingLesson(DrivingLessonDTO drivingLessonDTO) {
        this.lessonType = drivingLessonDTO.getLessonType();
        this.setBegin(drivingLessonDTO.getBegin());
        this.setEnd(drivingLessonDTO.getEnd());
        this.pickup = drivingLessonDTO.getPickup();
        this.destination = drivingLessonDTO.getDestination();
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public Boolean getManualLesson() {
        return manualLesson;
    }

    public DrivingLesson manualLesson(Boolean manualLesson) {
        this.manualLesson = manualLesson;
        return this;
    }

    public void setManualLesson(Boolean manualLesson) {
        this.manualLesson = manualLesson;
    }

    public Boolean getBookable() {
        return bookable;
    }

    public DrivingLesson bookable(Boolean bookable) {
        this.bookable = bookable;
        return this;
    }

    public void setBookable(Boolean bookable) {
        this.bookable = bookable;
    }

    public DrivingLessonType getLessonType() {
        return lessonType;
    }

    public DrivingLesson lessonType(DrivingLessonType lessonType) {
        this.lessonType = lessonType;
        return this;
    }

    public void setLessonType(DrivingLessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Student getDriver() {
        return driver;
    }

    public DrivingLesson driver(Student student) {
        this.driver = student;
        return this;
    }

    public void setDriver(Student student) {
        this.driver = student;
    }

    public Set<Student> getMissingStudents() {
        return missingStudents;
    }

    public DrivingLesson missingStudents(Set<Student> students) {
        this.missingStudents = students;
        return this;
    }

    public DrivingLesson addMissingStudent(Student student) {
        this.missingStudents.add(student);
        student.getMissedLessons().add(this);
        return this;
    }

    public DrivingLesson removeMissingStudent(Student student) {
        this.missingStudents.remove(student);
        student.getMissedLessons().remove(this);
        return this;
    }

    public void setMissingStudents(Set<Student> students) {
        this.missingStudents = students;
    }

    public Set<Student> getLateMissingStudents() {
        return lateMissingStudents;
    }

    public DrivingLesson lateMissingStudents(Set<Student> students) {
        this.lateMissingStudents = students;
        return this;
    }

    public DrivingLesson addLateMissingStudent(Student student) {
        this.lateMissingStudents.add(student);
        student.getLateMissedLessons().add(this);
        return this;
    }

    public DrivingLesson removeLateMissingStudent(Student student) {
        this.lateMissingStudents.remove(student);
        student.getLateMissedLessons().remove(this);
        return this;
    }

    public void setLateMissingStudents(Set<Student> students) {
        this.lateMissingStudents = students;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public DrivingLesson teacher(Teacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Location getPickup() {
        return pickup;
    }

    public DrivingLesson pickup(Location pickup) {
        this.pickup = pickup;
        return this;
    }

    public void setPickup(Location pickup) {
        this.pickup = pickup;
    }

    public Location getDestination() {
        return destination;
    }

    public DrivingLesson destination(Location destination) {
        this.destination = destination;
        return this;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Set<Student> getOptionalStudents() {
        return optionalStudents;
    }

    public DrivingLesson optionalStudents(Set<Student> students) {
        this.optionalStudents = students;
        return this;
    }

    public DrivingLesson addOptionalStudents(Student student) {
        this.optionalStudents.add(student);
        return this;
    }

    public DrivingLesson removeOptionalStudents(Student student) {
        this.optionalStudents.remove(student);
        return this;
    }

    public void setOptionalStudents(Set<Student> students) {
        this.optionalStudents = students;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "DrivingLesson{" +
            "manualLesson=" + manualLesson +
            ", bookable=" + bookable +
            ", lessonType=" + lessonType +
            ", driver=" + driver +
            ", missingStudents=" + missingStudents +
            ", lateMissingStudents=" + lateMissingStudents +
            ", teacher=" + teacher +
            ", pickup=" + pickup +
            ", destination=" + destination +
            ", optionalStudents=" + optionalStudents +
            "} " + super.toString();
    }
}
