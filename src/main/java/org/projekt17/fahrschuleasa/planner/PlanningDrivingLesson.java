package org.projekt17.fahrschuleasa.planner;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningStudent;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTimeSlot;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class PlanningDrivingLesson implements Comparable<PlanningDrivingLesson> {

    private long id;
    private PlanningTimeSlot slot;
    private PlanningStudent student;
    private List<PlanningStudent> possibleStudents;
    private boolean manualLesson = false;

    public PlanningDrivingLesson(long id, PlanningTimeSlot slot, PlanningStudent student, List<PlanningStudent> possibleStudents) {
        this.id = id;
        this.slot = slot;
        this.student = student;
        this.possibleStudents = possibleStudents;
    }

    public PlanningDrivingLesson(PlanningTimeSlot slot){
        this.id = slot.getId();
        this.slot = slot;
        this.student = null;
        this.possibleStudents = new ArrayList<>();
    }

    /**
     * This no arg constructor is needed for optaPlanner to create clones.
     */
    public PlanningDrivingLesson(){
    }

    public long getId() {
        return id;
    }

    public PlanningTimeSlot getSlot() {
        return slot;
    }

    public void setSlot(PlanningTimeSlot slot) {
        this.slot = slot;
    }

    /**
     * <p>
     *     This getter is only public due to the interaction which the optaplanner framework.
     *     From outside the planner you should rely on getStudentID.
     * </p>
     *
     * @return Return the planning instance, that is the PlanningStudent instance of the
     * respective student or null if no student is assigned to this PlanningDrivingLesson.
     */
    @PlanningVariable(nullable = true, valueRangeProviderRefs = {"studentRange"})
    public PlanningStudent getStudent() {
        return student;
    }

    /**
     * This method has to be used by the backend to write back the results of the planning!!!
     *
     * @return The Id of the student assigned to this driving lesson or null if no such assignment was made.
     */
    public Long getStudentId (){
        if (student == null)
            return null;
        else
            return student.getId();
    }

    public boolean isManualLesson() {
        return manualLesson;
    }

    @ValueRangeProvider(id = "studentRange")
    public List<PlanningStudent> getPossibleStudents() {
        return possibleStudents;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStudent(PlanningStudent student) {
        this.student = student;
    }

    public void setPossibleStudents(List<PlanningStudent> possibleStudents) {
        this.possibleStudents = possibleStudents;
    }

    public void addPossibleStudent(PlanningStudent student) {
        this.possibleStudents.add(student);
    }

    public void setManualLesson(boolean manualLesson) {
        this.manualLesson = manualLesson;
    }

    @Override
    public int compareTo(PlanningDrivingLesson o) {
        if (this.id == o.id) return 0;
        return this.id < o.id ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningDrivingLesson)) return false;
        PlanningDrivingLesson other = (PlanningDrivingLesson) o;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return slot.toString() + "; " + student.toString();
    }

}
