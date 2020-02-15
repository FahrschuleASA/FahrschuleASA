package org.projekt17.fahrschuleasa.planner.problemFacts;

import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class shall mimic the student class used by the main repository.
 * This is exclusively used for planning.
 */
public class PlanningStudent implements Serializable {

    private static final int WEIGHT_BASIC = 1;
    private static final int WEIGHT_ADVANCED = 1;
    private static final int WEIGHT_PERFORMANCE = 1;
    private static final int WEIGHT_INDEPENDENCE = 1;
    private static final int WEIGHT_OVERLAND = 1;
    private static final int WEIGHT_AUTOBAHN = 1;
    private static final int WEIGHT_NIGHT = 1;

    private long id;
    private boolean isActive;
    private DrivingCategory category;
    private int level;
    private PlanningTeacher teacher;
    private int wantedLessons;
    private List<PlanningPreference> preferences;
    private boolean preferencesChanged;
    private List<PlanningTimeSlot> oldSlots;

    public PlanningStudent() {}

    public PlanningStudent(long id, boolean isActive, DrivingCategory category, int level, PlanningTeacher teacher, int wantedLessons, List<PlanningPreference> preferences, boolean preferencesChanged, List<PlanningTimeSlot> oldSlots) {
        this.id = id;
        this.isActive = isActive;
        this.category = category;
        this.level = level;
        this.teacher = teacher;
        this.wantedLessons = wantedLessons;
        this.preferences = preferences;
        this.preferencesChanged = preferencesChanged;
        this.oldSlots = oldSlots;
    }

    public PlanningStudent(Student student, PlanningTeacher teacher) {
        this.id = student.getId();
        this.isActive = student.isActive();
        this.category = student.getCategory();
        this.level = computeLevel(student.getTeachingDiagram());
        this.teacher = teacher;
        this.wantedLessons = student.getWantedLessons();
        this.preferences = new ArrayList<>();
        this.preferencesChanged = student.isChangedPreferences();
        this.oldSlots = new ArrayList<>();

        for (Preference preference : student.getPreferences()) {
            PlanningTimeSlot referencedTimeSlot = getReferencedTimeSlot(preference.getTimeSlot().getId(), teacher.getSlots());
            if (referencedTimeSlot != null)
                this.preferences.add(new PlanningPreference(preference, referencedTimeSlot, this));
        }

        for (TimeSlot oldSlot : student.getLastScheduledTimeSlots()) {
            PlanningTimeSlot referencedTimeSlot = getReferencedTimeSlot(oldSlot.getId(), teacher.getSlots());
            if (referencedTimeSlot != null)
                this.oldSlots.add(referencedTimeSlot);
        }
    }

    /**
     * Computes the level from the given {@link TeachingDiagram}.
     *
     * @param statistics The given {@link TeachingDiagram}.
     * @return The computed level.
     */
    private int computeLevel(TeachingDiagram statistics) {
        if (statistics == null) return 0;
        int basic = statistics.getBasic() * WEIGHT_BASIC;
        int advanced = statistics.getAdvanced() * WEIGHT_ADVANCED;
        int performance = statistics.getPerformance() * WEIGHT_PERFORMANCE;
        int independence = statistics.getIndependence() * WEIGHT_INDEPENDENCE;
        int overland = statistics.getOverland() * WEIGHT_OVERLAND;
        int autobahn = statistics.getAutobahn() * WEIGHT_AUTOBAHN;
        int night = statistics.getNight() * WEIGHT_NIGHT;
        return basic + advanced + performance + independence + overland + autobahn + night;
    }

    /**
     * Returns the {@link PlanningTimeSlot} with the given id iff it is in the given slots.
     *
     * @param id The id to find.
     * @param slots The slots to search.
     * @return The {@link PlanningTimeSlot} with the given id or {@code null} iff it is not found in the given slots.
     */
    private PlanningTimeSlot getReferencedTimeSlot(long id, List<PlanningTimeSlot> slots) {
        for (PlanningTimeSlot slot : slots)
            if (id == slot.getId()) return slot;
        return null;
    }

    /**
     * Returns the {@link PlanningPreference} to the belonging slot.
     *
     * @param slot The slot to search.
     * @return The {@link PlanningPreference} to the given slot or {@code null} iff it is not found in the students preferences.
     */
    public PlanningPreference getBelongingPreference(PlanningTimeSlot slot) {
        for (PlanningPreference preference : preferences)
            if (slot.equals(preference.getSlot())) return preference;
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public DrivingCategory getCategory() {
        return category;
    }

    public int getLevel() {
        return level;
    }

    public PlanningTeacher getTeacher() {
        return teacher;
    }

    public int getWantedLessons() {
        return wantedLessons;
    }

    public List<PlanningPreference> getPreferences() {
        return preferences;
    }

    public boolean isPreferencesChanged() {
        return preferencesChanged;
    }

    public List<PlanningTimeSlot> getOldSlots() {
        return oldSlots;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCategory(DrivingCategory category) {
        this.category = category;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTeacher(PlanningTeacher teacher) {
        this.teacher = teacher;
    }

    public void setWantedLessons(int wantedLessons) {
        this.wantedLessons = wantedLessons;
    }

    public void decrementWantedLessons() {
        if (wantedLessons > 0) --wantedLessons;
    }

    public void setPreferences(List<PlanningPreference> preferences) {
        this.preferences = preferences;
    }

    public void addPreference(PlanningPreference preference) {
        preferences.add(preference);
    }

    public void setPreferencesChanged(boolean preferencesChanged) {
        this.preferencesChanged = preferencesChanged;
    }

    public void setOldSlots(List<PlanningTimeSlot> oldSlots) {
        this.oldSlots = oldSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningStudent)) return false;
        PlanningStudent other = (PlanningStudent) o;
        return this.id == other.id;
    }

    @Override
    public String toString(){
        return "student with id "+ id;
    }

}
