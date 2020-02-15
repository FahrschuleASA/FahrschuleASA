package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.Preference;

public class PreferenceDTO {

    private Long id;

    private Location pickup;

    private Location destination;

    private Long timeSlotId;

    private Long studentId;

    public PreferenceDTO(){}

    public PreferenceDTO(Preference preference) {
        id = preference.getId();
        pickup = preference.getPickup();
        destination = preference.getDestination();
        if (preference.getTimeSlot() != null)
            timeSlotId = preference.getTimeSlot().getId();

        if (preference.getStudent() != null)
            studentId = preference.getStudent().getId();
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    @Override
    public String toString() {
        return "PreferenceDTO{" +
            "id=" + id +
            ", pickup=" + pickup +
            ", destination=" + destination +
            ", timeSlotId=" + timeSlotId +
            ", studentId=" + studentId +
            '}';
    }
}
