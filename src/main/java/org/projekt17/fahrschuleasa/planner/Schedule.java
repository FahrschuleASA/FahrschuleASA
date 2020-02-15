package org.projekt17.fahrschuleasa.planner;


import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningPreference;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningStudent;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTeacher;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTimeSlot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@PlanningSolution
public class Schedule implements Serializable {

    private PlanningTeacher teacher;
    private List<PlanningStudent> students;
    private List<PlanningDrivingLesson> lessons;
    private List<PlanningDrivingLesson> blockedLessons;
    private List<PlanningDrivingLesson> allLessons;
    private int daysToPlan;

    private HardSoftScore score;

    public Schedule(PlanningTeacher teacher, List<PlanningStudent> students, List<PlanningDrivingLesson> scheduledLessons, List<PlanningDrivingLesson> blockedLessons, int daysToPlan, HardSoftScore score) {
        this.teacher = teacher;
        this.students = students;
        this.lessons = scheduledLessons;
        this.blockedLessons = blockedLessons;
        this.allLessons = new ArrayList<>();
        this.daysToPlan = daysToPlan;
        this.score = score;

        this.allLessons.addAll(this.lessons);
        this.allLessons.addAll(this.blockedLessons);

        this.allLessons.sort(null);
    }

    public Schedule(PlanningTeacher teacher, int daysToPlan){
        this.teacher = teacher;
        this.students = teacher.getStudents();
        this.lessons = new ArrayList<>();
        this.blockedLessons = teacher.getBlockedLessons();
        this.allLessons = new ArrayList<>();
        this.daysToPlan = daysToPlan;

        for (PlanningTimeSlot slot : teacher.getSlots())
            this.lessons.add(new PlanningDrivingLesson(slot));

        this.allLessons.addAll(this.lessons);
        this.allLessons.addAll(this.blockedLessons);

        this.allLessons.sort(null);

        computePossibleStudents();
    }

    /**
     * This no arg constructor is needed by optaPlanner to create clones.
     */
    public Schedule(){
    }

    /**
     * Compute all possible students for each time slot.
     * This is method is used to trim the search space drastically by
     * computing de facto hard constraints.
     */
    public void computePossibleStudents(){
        for (PlanningStudent student : students){
            DrivingCategory studentCategory = student.getCategory();
            List<PlanningPreference> preferences = student.getPreferences();
            for (PlanningPreference preference : preferences){
                PlanningTimeSlot preferredSlot = preference.getSlot();
                if (preferredSlot != null) {
                    long preferredSlotId = preferredSlot.getId();
                    for (PlanningDrivingLesson lesson : allLessons) {
                        if (lesson.getId() == preferredSlotId && lesson.getSlot().categoryIsPossible(studentCategory))
                            lesson.addPossibleStudent(student);
                    }
                }
            }
        }
    }

    public PlanningDrivingLesson getDrivingLessonById(Long id) {
        assert(id != null);

        int begin = 0;
        int end = allLessons.size() - 1;
        while (begin <= end) {
            int mid = (begin + end) / 2;
            PlanningDrivingLesson lesson = allLessons.get(mid);
            if (id == lesson.getId()) return lesson;
            if (id < lesson.getId())
                end = mid - 1;
            else
                begin = mid + 1;
        }
        return null;
    }

    public PlanningTeacher getTeacher() {
        return teacher;
    }

    @ProblemFactCollectionProperty
    public List<PlanningStudent> getStudents() {
        return students;
    }

    @PlanningEntityCollectionProperty
    public List<PlanningDrivingLesson> getLessons() {
        return lessons;
    }

    public List<PlanningDrivingLesson> getBlockedLessons() {
        return blockedLessons;
    }

    /**
     * This method has to be used by the backend to write back the results of the planning!!!
     *
     * @return The list of all lessons.
     */
    public List<PlanningDrivingLesson> getAllLessons() {
        return allLessons;
    }

    public int getDaysToPlan() {
        return daysToPlan;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setTeacher(PlanningTeacher teacher) {
        this.teacher = teacher;
    }

    public void setStudents(List<PlanningStudent> students) {
        this.students = students;
    }

    public void addStudent(PlanningStudent student) {
        students.add(student);
    }

    public void setLessons(List<PlanningDrivingLesson> lessons) {
        this.lessons = lessons;

        allLessons = new ArrayList<>();
        allLessons.addAll(this.lessons);
        allLessons.addAll(this.blockedLessons);
        allLessons.sort(null);
    }

    public void addLesson(PlanningDrivingLesson lesson) {
        lessons.add(lesson);

        allLessons.add(lesson);
        allLessons.sort(null);
    }

    public void setBlockedLessons(List<PlanningDrivingLesson> blockedLessons) {
        this.blockedLessons = blockedLessons;

        allLessons = new ArrayList<>();
        allLessons.addAll(this.lessons);
        allLessons.addAll(this.blockedLessons);
        allLessons.sort(null);
    }

    public void addBlockedLesson(PlanningDrivingLesson blockedLesson) {
        blockedLessons.add(blockedLesson);

        allLessons.add(blockedLesson);
        allLessons.sort(null);
    }

    public void setDaysToPlan(int daysToPlan) {
        this.daysToPlan = daysToPlan;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

}
