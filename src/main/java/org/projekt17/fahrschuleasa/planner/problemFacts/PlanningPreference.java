package org.projekt17.fahrschuleasa.planner.problemFacts;

import org.projekt17.fahrschuleasa.domain.Preference;

import java.io.Serializable;

/**
 * This class shall mimic the preference class used by the main repository.
 * This is exclusively used for planning.
 */
public class PlanningPreference implements Serializable {

    private long id;
    private PlanningTimeSlot slot;
    private PlanningLocation pickup;
    private PlanningLocation destination;
    private PlanningStudent student;

    public PlanningPreference(long id, PlanningTimeSlot slot, PlanningLocation pickup, PlanningLocation destination, PlanningStudent student) {
        this.id = id;
        this.slot = slot;
        this.pickup = pickup;
        this.destination = destination;
        this.student = student;
    }

    public PlanningPreference(Preference preference, PlanningTimeSlot referencedSlot, PlanningStudent student) {
        this.id = preference.getId();
        this.slot = referencedSlot;
        this.pickup = new PlanningLocation(preference.getPickup());
        this.destination = new PlanningLocation(preference.getDestination());
        this.student = student;
    }

    public long getId() {
        return id;
    }

    public PlanningTimeSlot getSlot() {
        return slot;
    }

    public PlanningLocation getPickup() {
        return pickup;
    }

    public PlanningLocation getDestination() {
        return destination;
    }

    public PlanningStudent getStudent() {
        return student;
    }

    public void setSlot(PlanningTimeSlot slot) {
        this.slot = slot;
    }

    public void setPickup(PlanningLocation pickup) {
        this.pickup = pickup;
    }

    public void setDestination(PlanningLocation destination) {
        this.destination = destination;
    }

    public void setStudent(PlanningStudent student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningPreference)) return false;
        PlanningPreference other = (PlanningPreference) o;
        return this.id == other.id;
    }

}
