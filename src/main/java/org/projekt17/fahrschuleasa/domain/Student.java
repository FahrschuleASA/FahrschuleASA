package org.projekt17.fahrschuleasa.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Student.
 */
@Entity
@Table(name = "student")
public class Student extends MyAccount {

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private DrivingCategory category;

    @Column(name = "ready_for_theory")
    private Boolean readyForTheory = false;

    @Column(name = "mission_accomplished")
    private Boolean missionAccomplished = false;

    @Column(name = "notify_free_lesson")
    private Boolean notifyForFreeLesson = true;

    @Column(name = "wanted_lessons")
    private Integer wantedLessons = SchoolConfiguration.getInitialLessons();

    @Column(name = "allowed_lessons")
    private Integer allowedLessons = SchoolConfiguration.getInitialLessons();

    @Column(name = "changed_preferences")
    private Boolean changedPreferences = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private TeachingDiagram teachingDiagram;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Preference> preferences = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "student_theory_lessons",
               joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "theory_lessons_id", referencedColumnName = "id"))
    private Set<TheoryLesson> theoryLessons = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("students")
    private Teacher teacher;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DrivingLesson> drivingLessons = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "student_missed_driving_lessons",
        joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "driving_lessons_id", referencedColumnName = "id"))
    private Set<DrivingLesson> missedLessons = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "student_late_missed_driving_lessons",
        joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "driving_lessons_id", referencedColumnName = "id"))
    private Set<DrivingLesson> lateMissedLessons = new HashSet<>();

    @OneToMany(mappedBy = "lastScheduledStudent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TimeSlot> lastScheduledTimeSlots = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public DrivingCategory getCategory() {
        return category;
    }

    public Student category(DrivingCategory category) {
        this.category = category;
        return this;
    }

    public void setCategory(DrivingCategory category) {
        this.category = category;
    }

    public Boolean isReadyForTheory() {
        return readyForTheory;
    }

    public Student readyForTheory(Boolean readyForTheory) {
        this.readyForTheory = readyForTheory;
        return this;
    }

    public void setReadyForTheory(Boolean readyForTheory) {
        this.readyForTheory = readyForTheory;
    }

    public Boolean getMissionAccomplished() {
        return missionAccomplished;
    }

    public Student missionAccomplished(Boolean missionAccomplished) {
        this.missionAccomplished = missionAccomplished;
        return this;
    }

    public void setMissionAccomplished(Boolean missionAccomplished) {
        this.missionAccomplished = missionAccomplished;
    }

    public Boolean getNotifyForFreeLesson() {
        return notifyForFreeLesson;
    }

    public Student notifyForFreeLesson(Boolean notifyForFreeLesson) {
        this.notifyForFreeLesson = notifyForFreeLesson;
        return this;
    }

    public void setNotifyForFreeLesson(Boolean notifyForFreeLesson) {
        this.notifyForFreeLesson = notifyForFreeLesson;
    }

    public Integer getWantedLessons() {
        return wantedLessons;
    }

    public Student wantedLessons(Integer wantedLessons) {
        this.wantedLessons = wantedLessons;
        return this;
    }

    public void setWantedLessons(Integer wantedLessons) {
        this.wantedLessons = wantedLessons;
    }

    public Integer getAllowedLessons() {
        return allowedLessons;
    }

    public Student allowedLessons(Integer allowedLessons) {
        this.allowedLessons = allowedLessons;
        return this;
    }

    public void setAllowedLessons(Integer allowedLessons) {
        this.allowedLessons = allowedLessons;
    }

    public Boolean isChangedPreferences() {
        return changedPreferences;
    }

    public Student changedPreferences(Boolean changedPreferences) {
        this.changedPreferences = changedPreferences;
        return this;
    }

    public void setChangedPreferences(Boolean changedPreferences) {
        this.changedPreferences = changedPreferences;
    }

    public TeachingDiagram getTeachingDiagram() {
        return teachingDiagram;
    }

    public Student teachingDiagram(TeachingDiagram teachingDiagram) {
        this.teachingDiagram = teachingDiagram;
        return this;
    }

    public void setTeachingDiagram(TeachingDiagram teachingDiagram) {
        this.teachingDiagram = teachingDiagram;
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public Student preferences(Set<Preference> preferences) {
        this.preferences = preferences;
        return this;
    }

    public Student addPreference(Preference preference) {
        this.preferences.add(preference);
        preference.setStudent(this);
        return this;
    }

    public Student removePreference(Preference preference) {
        this.preferences.remove(preference);
        preference.setStudent(null);
        return this;
    }

    public void setPreferences(Set<Preference> preferences) {
        this.preferences.forEach(p -> p.setStudent(null));
        this.preferences = preferences;
        this.preferences.forEach(p -> p.setStudent(this));
    }

    public Set<TheoryLesson> getTheoryLessons() {
        return theoryLessons;
    }

    public Student theoryLessons(Set<TheoryLesson> theoryLessons) {
        this.theoryLessons = theoryLessons;
        return this;
    }

    public Student addTheoryLessons(TheoryLesson theoryLesson) {
        this.theoryLessons.add(theoryLesson);
        theoryLesson.getStudents().add(this);
        return this;
    }

    public Student removeTheoryLessons(TheoryLesson theoryLesson) {
        this.theoryLessons.remove(theoryLesson);
        theoryLesson.getStudents().remove(this);
        return this;
    }

    public void setTheoryLessons(Set<TheoryLesson> theoryLessons) {
        this.theoryLessons = theoryLessons;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Student teacher(Teacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Set<DrivingLesson> getDrivingLessons() {
        return drivingLessons;
    }

    public Student drivingLessons(Set<DrivingLesson> drivingLessons) {
        this.drivingLessons = drivingLessons;
        return this;
    }

    public Student addDrivingLessons(DrivingLesson drivingLesson) {
        this.drivingLessons.add(drivingLesson);
        drivingLesson.setDriver(this);
        return this;
    }

    public Student removeDrivingLessons(DrivingLesson drivingLesson) {
        this.drivingLessons.remove(drivingLesson);
        drivingLesson.setDriver(null);
        return this;
    }

    public void setDrivingLessons(Set<DrivingLesson> drivingLessons) {
        this.drivingLessons = drivingLessons;
    }

    public Set<DrivingLesson> getMissedLessons() {
        return missedLessons;
    }

    public Student missedLessons(Set<DrivingLesson> drivingLessons) {
        this.missedLessons = drivingLessons;
        return this;
    }

    public Student addMissedLessons(DrivingLesson drivingLesson) {
        this.missedLessons.add(drivingLesson);
        drivingLesson.getMissingStudents().add(this);
        return this;
    }

    public Student removeMissedLessons(DrivingLesson drivingLesson) {
        this.missedLessons.remove(drivingLesson);
        drivingLesson.getMissingStudents().remove(this);
        return this;
    }

    public void setMissedLessons(Set<DrivingLesson> drivingLessons) {
        this.missedLessons = drivingLessons;
    }

    public Set<DrivingLesson> getLateMissedLessons() {
        return lateMissedLessons;
    }

    public Student lateMissedLessons(Set<DrivingLesson> drivingLessons) {
        this.lateMissedLessons = drivingLessons;
        return this;
    }

    public Student addLateMissedLessons(DrivingLesson drivingLesson) {
        this.lateMissedLessons.add(drivingLesson);
        drivingLesson.getLateMissingStudents().add(this);
        return this;
    }

    public Student removeLateMissedLessons(DrivingLesson drivingLesson) {
        this.lateMissedLessons.remove(drivingLesson);
        drivingLesson.getLateMissingStudents().remove(this);
        return this;
    }

    public void setLateMissedLessons(Set<DrivingLesson> drivingLessons) {
        this.lateMissedLessons = drivingLessons;
    }

    public Set<TimeSlot> getLastScheduledTimeSlots() {
        return lastScheduledTimeSlots;
    }

    public Student lastScheduledTimeSlots(Set<TimeSlot> lastScheduledTimeSlots) {
        this.lastScheduledTimeSlots = lastScheduledTimeSlots;
        return this;
    }

    public Student addLastScheduledTimeSlots(TimeSlot timeSlot) {
        this.lastScheduledTimeSlots.add(timeSlot);
        timeSlot.setLastScheduledStudent(this);
        return this;
    }

    public Student removeLastScheduledTimeSlots(TimeSlot timeSlot) {
        this.lastScheduledTimeSlots.remove(timeSlot);
        timeSlot.setLastScheduledStudent(null);
        return this;
    }

    public void setLastScheduledTimeSlots(Set<TimeSlot> lastScheduledTimeSlots) {
        this.lastScheduledTimeSlots.forEach(s -> s.setLastScheduledStudent(null));
        this.lastScheduledTimeSlots = lastScheduledTimeSlots;
        this.lastScheduledTimeSlots.forEach(s -> s.setLastScheduledStudent(this));
    }
// jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Student{" +
            "category=" + category +
            ", readyForTheory=" + readyForTheory +
            ", missionAccomplished=" + missionAccomplished +
            ", notifyForFreeLesson=" + notifyForFreeLesson +
            ", wantedLessons=" + wantedLessons +
            ", allowedLessons=" + allowedLessons +
            ", changedPreferences=" + changedPreferences +
            ", teachingDiagram=" + teachingDiagram +
            "} " + super.toString();
    }
}
