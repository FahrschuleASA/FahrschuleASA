package org.projekt17.fahrschuleasa.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Teacher.
 */
@Entity
@Table(name = "teacher")
public class Teacher extends MyAccount {

    @Column(name = "changed_time_slots")
    private Boolean changedTimeSlots = false;

    @Column(name = "school_owner")
    private Boolean schoolOwner;

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TimeSlot> timeSlots = new HashSet<>();

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TheoryLesson> theoryLessons = new HashSet<>();

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DrivingLesson> drivingLessons = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public Boolean isChangedTimeSlots() {
        return changedTimeSlots;
    }

    public Teacher changedTimeSlots(Boolean changedTimeSlots) {
        this.changedTimeSlots = changedTimeSlots;
        return this;
    }

    public void setChangedTimeSlots(Boolean changedTimeSlots) {
        this.changedTimeSlots = changedTimeSlots;
    }

    public Boolean isSchoolOwner() {
        return schoolOwner;
    }

    public Teacher schoolOwner(Boolean schoolOwner){
        this.schoolOwner = schoolOwner;
        return this;
    }

    public void setSchoolOwner(Boolean schoolOwner) {
        this.schoolOwner = schoolOwner;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Teacher students(Set<Student> students) {
        this.students = students;
        return this;
    }

    public Teacher addStudent(Student student) {
        this.students.add(student);
        student.setTeacher(this);
        return this;
    }

    public Teacher removeStudent(Student student) {
        this.students.remove(student);
        student.setTeacher(null);
        return this;
    }

    public void setStudents(Set<Student> students) {
        this.students.forEach(student -> student.setTeacher(null));
        this.students = students;
        this.students.forEach(student -> student.setTeacher(this));
    }

    public Set<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public Teacher timeSlots(Set<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
        return this;
    }

    public Teacher addTimeSlot(TimeSlot timeSlot) {
        this.timeSlots.add(timeSlot);
        timeSlot.setTeacher(this);
        return this;
    }

    public Teacher removeTimeSlot(TimeSlot timeSlot) {
        this.timeSlots.remove(timeSlot);
        timeSlot.setTeacher(null);
        return this;
    }

    public void setTimeSlots(Set<TimeSlot> timeSlots) {
        this.timeSlots.forEach(timeSlot -> timeSlot.setTeacher(null));
        this.timeSlots = timeSlots;
        this.timeSlots.forEach(timeSlot -> timeSlot.setTeacher(this));
    }

    public Set<TheoryLesson> getTheoryLessons() {
        return theoryLessons;
    }

    public Teacher theoryLessons(Set<TheoryLesson> theoryLessons) {
        this.theoryLessons = theoryLessons;
        return this;
    }

    public Teacher addTheoryLesson(TheoryLesson theoryLesson) {
        this.theoryLessons.add(theoryLesson);
        theoryLesson.setTeacher(this);
        return this;
    }

    public Teacher removeTheoryLesson(TheoryLesson theoryLesson) {
        this.theoryLessons.remove(theoryLesson);
        theoryLesson.setTeacher(null);
        return this;
    }

    public void setTheoryLessons(Set<TheoryLesson> theoryLessons) {
        this.theoryLessons.forEach(theoryLesson -> theoryLesson.setTeacher(null));
        this.theoryLessons = theoryLessons;
        this.theoryLessons.forEach(theoryLesson -> theoryLesson.setTeacher(this));
    }

    public Set<DrivingLesson> getDrivingLessons() {
        return drivingLessons;
    }

    public Teacher drivingLessons(Set<DrivingLesson> drivingLessons) {
        this.drivingLessons = drivingLessons;
        return this;
    }

    public Teacher addDrivingLesson(DrivingLesson drivingLesson) {
        this.drivingLessons.add(drivingLesson);
        drivingLesson.setTeacher(this);
        return this;
    }

    public Teacher removeDrivingLesson(DrivingLesson drivingLesson) {
        this.drivingLessons.remove(drivingLesson);
        drivingLesson.setTeacher(null);
        return this;
    }

    public void setDrivingLessons(Set<DrivingLesson> drivingLessons) {
        this.drivingLessons = drivingLessons;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public Optional<DrivingLesson> getLastDrivingLesson(LocalDate before) {
        LocalDateTime beforeTime = LocalDateTime.of(before, LocalTime.MIDNIGHT);
        return this.students.stream().map(student -> student.getDrivingLessons().stream()
                .filter(drivingLesson -> drivingLesson.getEnd().isBefore(beforeTime))
                .max(Comparator.comparing(DrivingLesson::getEnd)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .max(Comparator.comparing(DrivingLesson::getEnd));
    }

    public List<DrivingLesson> getBlockedLessons(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin.minusDays(1), LocalTime.MAX);
        LocalDateTime endTime = LocalDateTime.of(end.plusDays(1), LocalTime.MIDNIGHT);
        return this.students.stream().flatMap(student -> student.getDrivingLessons().stream()
                .filter(drivingLesson -> drivingLesson.getBegin().isAfter(beginTime) && drivingLesson.getEnd().isBefore(endTime)))
            .collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Teacher{" +
            "changedTimeSlots=" + changedTimeSlots +
            ", schoolOwner=" + schoolOwner +
            ", students=" + students +
            "} " + super.toString();
    }
}
