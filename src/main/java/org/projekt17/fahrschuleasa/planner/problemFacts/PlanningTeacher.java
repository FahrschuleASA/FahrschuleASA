package org.projekt17.fahrschuleasa.planner.problemFacts;

import org.projekt17.fahrschuleasa.config.PlannerConfiguration;
import org.projekt17.fahrschuleasa.domain.DrivingLesson;
import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.planner.PlanningDrivingLesson;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class shall mimic the teacher class used by the main repository.
 * This is exclusively used for planning.
 */
public class PlanningTeacher implements Serializable {

    private long id;
    private boolean isActive;
    private List<PlanningTimeSlot> slots;
    private List<PlanningDrivingLesson> blockedLessons;
    private List<PlanningTimeSlot> allSlots;
    private List<PlanningStudent> students;
    private boolean slotsChanged;
    private boolean isOwner;
    private int sundayRestTime; // in minutes

    public PlanningTeacher() {}

    public PlanningTeacher(long id, boolean isActive, List<PlanningTimeSlot> slots, List<PlanningDrivingLesson> blockedLessons, List<PlanningStudent> students, boolean slotsChanged, boolean isOwner, int sundayRestTime) {
        this.id = id;
        this.isActive = isActive;
        this.slots = slots;
        this.blockedLessons = blockedLessons;
        this.allSlots = new ArrayList<>();
        this.students = students;
        this.slotsChanged = slotsChanged;
        this.isOwner = isOwner;
        this.sundayRestTime = sundayRestTime;

        this.allSlots.addAll(this.slots);
        for (PlanningDrivingLesson blockedLesson : this.blockedLessons)
            this.allSlots.add(blockedLesson.getSlot());
    }

    public PlanningTeacher(Teacher teacher, LocalDate startDate){
        this.id = teacher.getId();
        this.isActive = teacher.isActive();
        this.slots = new ArrayList<>();
        this.blockedLessons = new ArrayList<>();
        this.allSlots = new ArrayList<>();
        this.students = new ArrayList<>();
        this.slotsChanged = teacher.isChangedTimeSlots();
        this.isOwner = teacher.isSchoolOwner();
        this.sundayRestTime = computeRestTime(teacher.getLastDrivingLesson(startDate), startDate);

        long lastSlotId = 0L;
        for (TimeSlot slot : teacher.getTimeSlots()) {
            if (!slotIsBlocked(slot, startDate))
                this.slots.add(new PlanningTimeSlot(slot));
            lastSlotId = Math.max(lastSlotId, slot.getId());
        }

        for (Student student : teacher.getStudents()) {
            if (student.isActive())
                this.students.add(new PlanningStudent(student, this));
        }

        long preferenceId = 0L;
        for (DrivingLesson blockedLesson : teacher.getBlockedLessons(startDate, startDate.plusDays(6L))) {
            LocalDateTime begin = blockedLesson.getBegin();
            LocalDateTime end = blockedLesson.getEnd();
            int beginTime = begin.getHour() * 60 + begin.getMinute();
            int endTime = end.getHour() * 60 + end.getMinute();
            int day = begin.toLocalDate().until(startDate).getDays();
            PlanningTimeSlot slot = new PlanningTimeSlot(++lastSlotId, beginTime, endTime, day, Stream.of(blockedLesson.getDriver().getCategory()).collect(Collectors.toList()), new ArrayList<>());

            PlanningStudent student = findStudentById(blockedLesson.getDriver().getId());
            assert(student != null);

            PlanningLocation pickup = new PlanningLocation(blockedLesson.getPickup());
            PlanningLocation destination = new PlanningLocation(blockedLesson.getDestination());
            PlanningPreference preference = new PlanningPreference(--preferenceId, slot, pickup, destination, student);
            student.addPreference(preference);

            PlanningDrivingLesson blockedPlanningLesson = new PlanningDrivingLesson(slot);
            blockedPlanningLesson.setStudent(student);
            blockedPlanningLesson.setManualLesson(true);
            this.blockedLessons.add(blockedPlanningLesson);
        }

        this.allSlots.addAll(this.slots);
        for (PlanningDrivingLesson blockedLesson : this.blockedLessons)
            this.allSlots.add(blockedLesson.getSlot());
    }

    /**
     * The rest time can be negative iff the last scheduled driving lesson is beyond sunday midnight
     */
    private int computeRestTime(Optional<DrivingLesson> lastScheduledLesson, LocalDate startDate) {
        if (lastScheduledLesson.isPresent()){
            LocalDateTime endOfLesson = lastScheduledLesson.get().getEnd();
            int daysToMonday = endOfLesson.toLocalDate().until(startDate).getDays();
            int minutesOfDay = endOfLesson.getHour() * 60 + endOfLesson.getMinute();
            return 24 * 60 * daysToMonday -  minutesOfDay;
        }
        else return PlannerConfiguration.getMinimalRestTime();
    }

    private boolean slotIsBlocked(TimeSlot slot, LocalDate startDate) {
        for (LocalDate blockedDate : slot.getBlockedDates()) {
            if (blockedDate.isAfter(startDate.minusDays(1)) && blockedDate.isBefore(startDate.plusDays(7)))
                return true;
        }
        return false;
    }

    private PlanningStudent findStudentById(long id) {
        for (PlanningStudent student : this.students) {
            if (student.getId() == id) return student;
        }
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

    public List<PlanningTimeSlot> getSlots() {
        return slots;
    }

    public List<PlanningDrivingLesson> getBlockedLessons() {
        return blockedLessons;
    }

    public List<PlanningTimeSlot> getAllSlots() {
        return allSlots;
    }

    public List<PlanningStudent> getStudents() {
        return students;
    }

    public boolean isSlotsChanged() {
        return slotsChanged;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public int getSundayRestTime() {
        return sundayRestTime;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setSlots(List<PlanningTimeSlot> slots) {
        this.slots = slots;

        allSlots = new ArrayList<>();
        allSlots.addAll(this.slots);
        for (PlanningDrivingLesson blockedLesson : blockedLessons)
            allSlots.add(blockedLesson.getSlot());
    }

    public void deleteSlots(List<PlanningTimeSlot> slots) {
        this.slots.removeAll(slots);
        allSlots.removeAll(slots);
    }

    public void setBlockedLessons(List<PlanningDrivingLesson> blockedLessons) {
        this.blockedLessons = blockedLessons;

        allSlots = new ArrayList<>();
        allSlots.addAll(slots);
        for (PlanningDrivingLesson blockedLesson : this.blockedLessons)
            allSlots.add(blockedLesson.getSlot());
    }

    public void addBlockedLesson(PlanningDrivingLesson lesson) {
        blockedLessons.add(lesson);
        allSlots.add(lesson.getSlot());
    }

    public void setStudents(List<PlanningStudent> students) {
        this.students = students;
    }

    public void setSlotsChanged(boolean slotsChanged) {
        this.slotsChanged = slotsChanged;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public void setSundayRestTime(int sundayRestTime) {
        this.sundayRestTime = sundayRestTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanningTeacher)) return false;
        PlanningTeacher other = (PlanningTeacher) o;
        return this.id == other.id;
    }

}
