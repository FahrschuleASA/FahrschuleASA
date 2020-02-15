package org.projekt17.fahrschuleasa.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.projekt17.fahrschuleasa.service.dto.PreferenceDTO;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A Preference.
 */
@Entity
@Table(name = "preference")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Preference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("preferences")
    private Location pickup;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("preferences")
    private Location destination;

    @ManyToOne
    @JsonIgnoreProperties("preferences")
    private TimeSlot timeSlot;

    @ManyToOne
    @JsonIgnoreProperties("preferences")
    private Student student;

    public Preference() {}

    public Preference(PreferenceDTO preferenceDTO) {
        this.pickup = preferenceDTO.getPickup();
        this.destination = preferenceDTO.getDestination();
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getPickup() {
        return pickup;
    }

    public Preference pickup(Location location) {
        this.pickup = location;
        return this;
    }

    public void setPickup(Location location) {
        this.pickup = location;
    }

    public Location getDestination() {
        return destination;
    }

    public Preference destination(Location location) {
        this.destination = location;
        return this;
    }

    public void setDestination(Location location) {
        this.destination = location;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Preference timeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
        return this;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Student getStudent() {
        return student;
    }

    public Preference student(Student student) {
        this.student = student;
        return this;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Preference)) {
            return false;
        }
        return id != null && id.equals(((Preference) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Preference{" +
            "id=" + id +
            ", pickup=" + pickup +
            ", destination=" + destination +
            ", timeSlot=" + timeSlot +
            ", student=" + student +
            '}';
    }
}
