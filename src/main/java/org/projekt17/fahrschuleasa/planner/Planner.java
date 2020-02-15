package org.projekt17.fahrschuleasa.planner;


import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.projekt17.fahrschuleasa.config.PlannerConfiguration;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningStudent;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTeacher;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTimeSlot;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Planner implements Serializable {

    private PlanningTeacher teacher;
    private int daysToPlan;

    /**
     * The following parameters can be chosen by the owner.
     */
    private final static int CONSECUTIVE_LESSONS_PARAM = PlannerConfiguration.getConsecutiveLessonsParameter(); // measured in minutes
    private static final int WEIGHT_BASIC = PlannerConfiguration.getWeightBasic();
    private static final int WEIGHT_ADVANCED = PlannerConfiguration.getWeightAdvanced();
    private static final int WEIGHT_PERFORMANCE = PlannerConfiguration.getWeightPerformance();
    private static final int WEIGHT_INDEPENDENCE = PlannerConfiguration.getWeightIndependence();
    private static final int WEIGHT_OVERLAND = PlannerConfiguration.getWeightOverland();
    private static final int WEIGHT_AUTOBAHN = PlannerConfiguration.getWeightAutobahn();
    private static final int WEIGHT_NIGHT = PlannerConfiguration.getWeightNight();

    /**
     * The following parameter is used for the computeDifferenceOfWantedLessons() method.
     */
    private final static double DIFF_WANTED_LESSONS_PARAM = PlannerConfiguration.getDiffWantedLessonsParameter();

    /**
     * The following parameters are given by legal requirements.
     */
    private final static int MAXIMAL_TIME_OWNER = PlannerConfiguration.getMaximalTimeOwner(); // measured in minutes
    private final static int MAXIMAL_TIME_TEACHER = PlannerConfiguration.getMaximalTimeTeacher(); // measured in minutes
    private final static int MINIMAl_REST_TIME = PlannerConfiguration.getMinimalRestTime(); // measured in minutes

    /**
     * This fields are precomputed for the ScoreCalculator
     */
    private static int numberOfSlots;
    private static List<List<Long>> consecutiveSlots;
    private static int[] lessonsPerDay;
    private static double maxDifferenceOfWantedLessons;
    private static int maxStudentsWithMultipleLessons;
    private static int maxVehicleChanges;
    private static int maxDistanceTransgressions;
    private static int maxBlocks;
    //private static int maxIdleTime;
    private static int maxLevel;

    public Planner(PlanningTeacher teacher, int daysToPlan) {
        this.teacher = teacher;
        this.daysToPlan = daysToPlan;
    }

    public Planner(PlanningTeacher teacher) {
        this.teacher = teacher;
        this.daysToPlan = 7;
    }

    public Planner(Teacher teacher, LocalDate startDate, int daysToPlan) {
        this.teacher = new PlanningTeacher(teacher, startDate);
        this.daysToPlan = daysToPlan;
    }

    public Planner(Teacher teacher, LocalDate startDate) {
        this.teacher = new PlanningTeacher(teacher, startDate);
        this.daysToPlan = 7;
    }

    /**
     * This function shall be called by the backend to compute a plan.
     * It requires an instance of a teacher.
     * <b>
     * The caller must make sure that no manipulations of any planning relevant data takes place during computation.
     * It lies completely within the responsibility of the caller to include the returned schedule into the database.
     * This planner will not change anything which is not included in this package.
     * </b>
     *
     * @return Return the schedule.
     */
    public Schedule makeSchedule() {
        if (!teacher.isSlotsChanged())
            computePartialScheduleAndMinimizeTask();

        computeMaxValues();

        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource("config/plannerConfig.xml");
        Solver<Schedule> solver = solverFactory.buildSolver();
        solver.solve(createModel());
        Schedule solution = solver.getBestSolution();

        printResults(solution);

        return solution;
    }

    public Schedule makeQuickSchedule() {
        if (!teacher.isSlotsChanged())
            computePartialScheduleAndMinimizeTask();
        computeMaxValues();

        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource("config/plannerQuickConfig.xml");
        Solver<Schedule> solver = solverFactory.buildSolver();
        solver.solve(createModel());
        Schedule solution = solver.getBestSolution();

        printResults(solution);

        return solution;
    }

    private void computeMaxValues() {
        numberOfSlots = teacher.getSlots().size();
        consecutiveSlots = computeConsecutiveSlotIds();
        lessonsPerDay = computeLessonsPerDay();
        maxDifferenceOfWantedLessons = computeMaxDifferenceOfWantedLessons();
        maxStudentsWithMultipleLessons = computeMaxMultipleLessons();
        maxVehicleChanges = computeMaxVehicleChanges();
        maxDistanceTransgressions = computeMaxDistanceTransgressions();
        maxBlocks = computeMaxBlocks();
        //maxIdleTime = computeMaxIdleTime();
        maxLevel = 100 * (WEIGHT_BASIC + WEIGHT_ADVANCED + WEIGHT_PERFORMANCE + WEIGHT_INDEPENDENCE + WEIGHT_OVERLAND + WEIGHT_AUTOBAHN + WEIGHT_NIGHT);
    }

    /**
     * Computes a partial schedule which is not computed again because it can be adopted
     * from the last planning task.
     * Simultaneous, the planning facts are minimized e.g. the parts which will not be computed again are deleted.
     */
    private void computePartialScheduleAndMinimizeTask() {
        List<PlanningTimeSlot> slotsToRemove = new ArrayList<>();

        for (PlanningStudent student : teacher.getStudents()) {
            if (!student.isPreferencesChanged()) {
                for (PlanningTimeSlot oldSlot : student.getOldSlots()) {
                    if (slotIsPossible(oldSlot)) {
                        PlanningDrivingLesson oldLesson = new PlanningDrivingLesson(oldSlot);
                        oldLesson.setStudent(student);
                        student.decrementWantedLessons();
                        teacher.addBlockedLesson(oldLesson);
                        slotsToRemove.add(oldSlot);
                    }
                }
            }
        }

        teacher.deleteSlots(slotsToRemove);
    }

    private boolean slotIsPossible(PlanningTimeSlot slot) {
        return !slotIsBlocked(slot) && slotFulfilsMaximalTime(slot) && slotFulfilsRestTime(slot);
    }

    private boolean slotIsBlocked(PlanningTimeSlot slot) {
        for (PlanningDrivingLesson blockedLesson : teacher.getBlockedLessons()) {
            PlanningTimeSlot blockedSlot = blockedLesson.getSlot();
            if (slot.overlaps(blockedSlot)) return true;
        }
        return false;
    }

    private boolean slotFulfilsMaximalTime(PlanningTimeSlot slot) {
        int timeOfDay = slot.getEnd() - slot.getBegin();

        int day = slot.getDay();
        for (PlanningDrivingLesson blockedLesson : teacher.getBlockedLessons()) {
            PlanningTimeSlot blockedSlot = blockedLesson.getSlot();
            int blockedDay = blockedSlot.getDay();
            int blockedBegin = blockedSlot.getBegin();
            int blockedEnd = blockedSlot.getEnd();
            if (blockedDay == day)
                timeOfDay += blockedBegin > blockedEnd ? 24 * 60 - blockedBegin : blockedEnd - blockedBegin;
            if (blockedDay == day - 1)
                timeOfDay += blockedBegin > blockedEnd ? blockedEnd : 0;
        }

        if (teacher.isOwner())
            return timeOfDay < MAXIMAL_TIME_OWNER;
        else
            return timeOfDay < MAXIMAL_TIME_TEACHER;
    }

    private boolean slotFulfilsRestTime(PlanningTimeSlot slot) {
        int day = slot.getDay();
        int begin = slot.getBegin();
        if (day == 0 && begin + teacher.getSundayRestTime() < MINIMAl_REST_TIME) return false;
        for (PlanningDrivingLesson blockedLesson : teacher.getBlockedLessons()) {
            if (blockedLesson.isManualLesson()) {
                PlanningTimeSlot blockedSlot = blockedLesson.getSlot();
                int blockedDay = blockedSlot.getDay();
                int blockedBegin = blockedSlot.getBegin();
                int blockedEnd = blockedSlot.getEnd();
                if (blockedDay == day - 1 && blockedBegin > 6 * 60) {
                    int restTime = blockedBegin > blockedEnd ? begin - blockedEnd : 24 * 60 - blockedEnd + begin;
                    if (restTime < MINIMAl_REST_TIME) return false;
                }
                if (blockedDay == day && blockedBegin < blockedEnd && blockedEnd < 6 * 60)
                    if (begin - blockedEnd < MINIMAl_REST_TIME) return false;
                if (blockedDay == day + 1 && blockedBegin > 6 * 60)
                    if (24 * 60 - slot.getEnd() + blockedBegin < MINIMAl_REST_TIME) return false;
            }
        }
        return true;
    }

    /**
     * Builds a model of the planning task for a teacher.
     * This model is manipulated by the solver and turned into a planning solution
     *
     * @return An empty schedule model containing all time slots and students to assign.
     */
    private Schedule createModel() {
        return new Schedule(teacher, daysToPlan);
    }

    /**
     * This function is a private helper function for displaying the results.
     * It is only needed for debugging and can be deleted afterwards.
     *
     * @param schedule The schedule as computed by the planner.
     */
    private void printResults(Schedule schedule) {

        System.out.println("The teacher selected " + teacher.getSlots().size() + " slots.");

        if (schedule.getScore().isFeasible())
            System.out.println("Planning was successful!\n Writing back results now...");
        else
            System.out.println("The given task is not feasible (at least in the given time).\n" + "Falling back to best guess solution fragment.");

        for (PlanningDrivingLesson lesson : schedule.getAllLessons()) {
            if (lesson.getStudentId() == null) {
                System.out.println("Could not assign driving lesson with id " + lesson.getId() + " to any student.");
                int possibleStudents = lesson.getPossibleStudents().size();
                System.out.println("\t There are " + possibleStudents + " possible students for time slot "+ lesson.getSlot().getId());

                /*if (lesson.getPossibleStudents().size() > 0) {
                    System.out.println("There are " + possibleStudents + " possible students for this slot");
                    lesson.getPossibleStudents().forEach(System.out::println);
                    System.out.println();
                }*/
            } else
                System.out.println("To " + lesson.getSlot().toString() + " assign student with id " + lesson.getStudentId() + ".");
        }

        System.out.println("Planning results are written back.");
    }

    /**
     * Computes if two given lessons are consecutive (in terms of the CONSECUTIVE_LESSON_PARAM).
     * If lesson1 is later than lesson2 the result is false!
     */
    private boolean lessonsAreConsecutive(PlanningTimeSlot slot1, PlanningTimeSlot slot2) {
        int day1 = slot1.getDay();
        int day2 = slot2.getDay();
        if (day1 > day2 || day2 - day1 > 1) return false;
        if (day2 - day1 == 1) return 24 * 60 - slot1.getEnd() + slot2.getBegin() < CONSECUTIVE_LESSONS_PARAM;
        return slot2.getBegin() - slot1.getEnd() < CONSECUTIVE_LESSONS_PARAM;
    }

    /**
     * Computes the consecutive lessons no matter if they are assigned to a student or not.
     * The consecutive lessons are represented as list of lists of driving lessons.
     * The inner lists are consecutive lessons in each case.
     * The outer list is only used as a combination of consecutive lessons.
     */
    private List<List<Long>> computeConsecutiveSlotIds() {
        List<List<Long>> consecutiveSlotsIds = new ArrayList<>();
        List<PlanningTimeSlot> slots = teacher.getAllSlots();
        if (slots.isEmpty()) return consecutiveSlotsIds;
        slots.sort(null);

        PlanningTimeSlot currentSlot = slots.get(0);
        boolean consecutive = false;
        for (int i = 1; i < slots.size(); ++i) {
            PlanningTimeSlot nextSlot = slots.get(i);
            if (lessonsAreConsecutive(currentSlot, nextSlot)) {
                if (consecutive) {
                    consecutiveSlotsIds.get(consecutiveSlotsIds.size() - 1).add(nextSlot.getId());
                } else {
                    consecutive = true;
                    List<Long> consecutiveSlotsList = new ArrayList<>();
                    consecutiveSlotsList.add(currentSlot.getId());
                    consecutiveSlotsList.add(nextSlot.getId());
                    consecutiveSlotsIds.add(consecutiveSlotsList);
                }
            } else
                consecutive = false;
            currentSlot = nextSlot;
        }
        return consecutiveSlotsIds;
    }

    private int[] computeLessonsPerDay() {
        int[] lessonsPerDay = new int[daysToPlan];
        for (PlanningTimeSlot slot : teacher.getAllSlots()) {
            int day = slot.getDay();
            if (slot.getEnd() <= 6 * 60 && slot.getBegin() <= slot.getEnd())
                if (day - 1 >= 0) ++lessonsPerDay[day - 1];
                else
                    ++lessonsPerDay[day];
        }
        return lessonsPerDay;
    }

    private int computeMaxMultipleLessons() {
        int maxMultipleLessons = 0;
        for (int lessonsOneDay : lessonsPerDay)
            maxMultipleLessons += Math.min(lessonsOneDay / 2, teacher.getStudents().size());
        return maxMultipleLessons;
    }

    private double computeMaxDifferenceOfWantedLessons() {
        double maxDifferenceOfWantedLessons = 0.0;
        for (PlanningStudent student : teacher.getStudents()) {
            int maxDifference = Math.max(1, student.getWantedLessons());
            maxDifferenceOfWantedLessons += Math.pow(maxDifference, DIFF_WANTED_LESSONS_PARAM);
        }
        return maxDifferenceOfWantedLessons;
    }

    private int computeMaxVehicleChanges() {
        if (DrivingCategory.values().length < 2) return 0;

        int maxVehicleChanges = 0;
        for (List<Long> consecutiveSlotsList : consecutiveSlots)
            maxVehicleChanges += consecutiveSlotsList.size() - 1;
        return maxVehicleChanges;
    }

    private int computeMaxDistanceTransgressions() {
        int maxDistanceTransgressions = 0;
        for (List<Long> consecutiveSlotsList : consecutiveSlots) {
            maxDistanceTransgressions += consecutiveSlotsList.size() - 1;
        }
        return 3 * maxDistanceTransgressions;
    }

    private int computeMaxBlocks() {
        int maxBlocks = 0;
        for (List<Long> consecutiveSlotsList : consecutiveSlots) {
            maxBlocks += consecutiveSlotsList.size() - 1;
        }
        return maxBlocks;
    }

    /*private int computeMaxIdleTime() {
        int maxIdleTime = 0;
        for (int lessonsOneDay : lessonsPerDay)
            if (lessonsOneDay > 2) maxIdleTime += lessonsOneDay - 2;
        return maxIdleTime;
    }*/

    public static int getNumberOfSlots() {
        return numberOfSlots;
    }

    public static List<List<Long>> getConsecutiveSlots() {
        return consecutiveSlots;
    }

    public static double getMaxDifferenceOfWantedLessons() {
        return maxDifferenceOfWantedLessons;
    }

    public static int getMaxStudentsWithMultipleLessons() {
        return maxStudentsWithMultipleLessons;
    }

    public static int getMaxVehicleChanges() {
        return maxVehicleChanges;
    }

    public static int getMaxDistanceTransgressions() {
        return maxDistanceTransgressions;
    }

    public static int getMaxBlocks() {
        return maxBlocks;
    }

    /*public static int getMaxIdleTime() {
        return maxIdleTime;
    }*/

    public static int getMaxLevel() {
        return maxLevel;
    }

}
