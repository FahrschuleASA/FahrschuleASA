package org.projekt17.fahrschuleasa.planner.problemFacts;

import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This class shall mimic the time slot class used by the main repository.
 * This is exclusively used for planning.
 */
public class PlanningTimeSlot implements Serializable, Comparable<PlanningTimeSlot> {

    private long id;
    private int begin; // in minutes of the day e.g. 10:15h = 10 * 60 + 15 = 615
    private int end; // similar to begin
    private int day; // 0 = MO, 1 = TU, ..., 6 = SU
    private List<DrivingCategory> categoryPreference;
    private List<DrivingCategory> categoryOptional;

    public PlanningTimeSlot() {}

    public PlanningTimeSlot(long id, int begin, int end, int day, List<DrivingCategory> categoryPreference, List<DrivingCategory> categoryOptional) {
        this.id = id;
        this.begin = begin;
        this.end = end;
        this.day = day;
        this.categoryPreference = categoryPreference;
        this.categoryOptional = categoryOptional;
    }

    public PlanningTimeSlot(TimeSlot timeSlot) {
        this.id = timeSlot.getId();
        this.begin = timeSlot.getBegin();
        this.end = timeSlot.getEnd();
        this.day = timeSlot.getDay().ordinal();
        this.categoryPreference = new ArrayList<>(timeSlot.getPreferredCategories());
        this.categoryOptional = new ArrayList<>(timeSlot.getOptionalCategories());
    }

    public boolean categoryIsPreference(DrivingCategory category) {
        return categoryPreference.contains(category);
    }

    public boolean categoryIsOptional(DrivingCategory category) {
        return categoryOptional.contains(category);
    }

    public boolean categoryIsPossible(DrivingCategory category) {
        return categoryIsPreference(category) || categoryIsOptional(category);
    }

    public int getGlobalBegin() {
        return 24 * 60 * day + begin;
    }

    public int getGlobalEnd() {
        if (begin > end) return 24 * 60 * (day + 1) + end;
        else return 24 * 60 * day + end;
    }

    public boolean overlaps(PlanningTimeSlot other) {
        int thisGlobalBegin = this.getGlobalBegin();
        int otherGlobalBegin = other.getGlobalBegin();

        if (thisGlobalBegin == otherGlobalBegin) return true;

        if (thisGlobalBegin < otherGlobalBegin)
            return otherGlobalBegin < this.getGlobalEnd();
        else
            return thisGlobalBegin < other.getGlobalEnd();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getDay() {
        return day;
    }

    public List<DrivingCategory> getCategoryPreference() {
        return categoryPreference;
    }

    public List<DrivingCategory> getCategoryOptional() {
        return categoryOptional;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setCategoryPreference(List<DrivingCategory> categoryPreference) {
        this.categoryPreference = categoryPreference;
    }

    public void setCategoryOptional(List<DrivingCategory> categoryOptional) {
        this.categoryOptional = categoryOptional;
    }

    @Override
    public int compareTo(PlanningTimeSlot o) {
        if (this.getGlobalBegin() == o.getGlobalBegin()) {
            if (this.getGlobalEnd() == o.getGlobalEnd()) return 0;
            return this.getGlobalEnd() < o.getGlobalEnd() ? -1 : 1;
        }
        return this.getGlobalBegin() < o.getGlobalBegin() ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningTimeSlot)) return false;
        PlanningTimeSlot other = (PlanningTimeSlot) o;
        return this.id == other.id;
    }

    @Override
    public String toString(){
        String string = ("the time slot with id " + id + " on ");
        switch (day) {
            case 0: string += "monday "; break;
            case 1: string += "tuesday "; break;
            case 2: string += "wednesday "; break;
            case 3: string += "thursday "; break;
            case 4: string += "friday "; break;
            case 5: string += "saturday "; break;
            case 6: string += "sunday "; break;
            default: string += "wrong day ";
        }
        string += String.format("(%02d:%02d)-(%02d:%02d)", begin / 60, begin % 60, end / 60, end % 60);
        return string;
    }

}
