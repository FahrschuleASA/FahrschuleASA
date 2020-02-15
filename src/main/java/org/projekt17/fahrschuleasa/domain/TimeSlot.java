package org.projekt17.fahrschuleasa.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A TimeSlot.
 */
@Entity
@Table(name = "time_slot")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "begin")
    private Integer begin;

    @Column(name = "end")
    private Integer end;

    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private DayOfWeek day;

    @ManyToOne
    @JsonIgnoreProperties("timeSlots")
    private Teacher teacher;

    @ManyToOne
    @JsonIgnoreProperties("timeSlots")
    private Student lastScheduledStudent;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private Set<DrivingCategory> preferredCategories = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private Set<DrivingCategory> optionalCategories = new HashSet<>();

    @ElementCollection
    private Set<LocalDate> blockedDates = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBegin() {
        return begin;
    }

    public TimeSlot begin(Integer begin) {
        this.begin = begin;
        return this;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return end;
    }

    public TimeSlot end(Integer end) {
        this.end = end;
        return this;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public TimeSlot day(DayOfWeek day) {
        this.day = day;
        return this;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public TimeSlot teacher(Teacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getLastScheduledStudent() {
        return lastScheduledStudent;
    }

    public TimeSlot lastScheduledStudent(Student lastScheduledStudent) {
        this.lastScheduledStudent = lastScheduledStudent;
        return this;
    }

    public void setLastScheduledStudent(Student lastScheduledStudent) {
        this.lastScheduledStudent = lastScheduledStudent;
    }
// jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public Set<DrivingCategory> getPreferredCategories() {
        return preferredCategories;
    }

    public TimeSlot preferredCategories(Set<DrivingCategory> preferedCategories) {
        this.preferredCategories = preferedCategories;
        return this;
    }

    public void setPreferredCategories(Set<DrivingCategory> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public Set<DrivingCategory> getOptionalCategories() {
        return optionalCategories;
    }

    public TimeSlot optionalCategories(Set<DrivingCategory> optionalCategories) {
        this.optionalCategories = optionalCategories;
        return this;
    }

    public void setOptionalCategories(Set<DrivingCategory> optionalCategories) {
        this.optionalCategories = optionalCategories;
    }

    public Set<LocalDate> getBlockedDates() {
        return blockedDates;
    }

    public TimeSlot blockedDates(Set<LocalDate> blockedDates) {
        this.blockedDates = blockedDates;
        return this;
    }

    public void setBlockedDates(Set<LocalDate> blockedDates) {
        this.blockedDates = blockedDates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeSlot)) {
            return false;
        }
        return id != null && id.equals(((TimeSlot) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
            "id=" + id +
            ", begin=" + begin +
            ", end=" + end +
            ", day=" + day +
            ", teacher=" + teacher +
            ", preferredCategories=" + preferredCategories +
            ", optionalCategories=" + optionalCategories +
            ", blockedDates=" + blockedDates +
            '}';
    }
}
