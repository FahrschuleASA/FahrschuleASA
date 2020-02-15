package org.projekt17.fahrschuleasa.planner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.projekt17.fahrschuleasa.config.PlannerConfiguration;
import org.projekt17.fahrschuleasa.planner.problemFacts.*;

import java.util.ArrayList;
import java.util.List;

public class ScoreCalculator implements EasyScoreCalculator<Schedule> {

    /**
     * The following parameters are precomputed by the Planner
     */
    private static int NUMBER_OF_LESSONS;
    private static List<List<Long>> CONSECUTIVE_LESSONS;

    /**
     * The following upper bounds are precomputed by the Planner.
     */
    private static double MAX_DIFF_WANTED_LESSONS;
    private static int MAX_MULTIPLE_LESSONS;
    private static int MAX_VEHICLE_CHANGES;
    private static int MAX_DISTANCE;
    private static int MAX_IDLE_TIME;
    private static int MAX_BLOCKS;
    private static int MAX_LEVEL;

    /**
     * The following parameters are given by legal requirements.
     */
    private final static int MAXIMAL_TIME_OWNER = PlannerConfiguration.getMaximalTimeOwner(); // measured in minutes
    private final static int MAXIMAL_TIME_TEACHER = PlannerConfiguration.getMaximalTimeTeacher(); // measured in minutes
    private final static int MINIMAl_REST_TIME = PlannerConfiguration.getMinimalRestTime(); // measured in minutes

    /**
     * The following parameters can be chosen by the owner.
     */
    private final static double DISTANCE_PARAM_SOFT = PlannerConfiguration.getDiffParameterSoft(); // measured in km
    private final static double DISTANCE_PARAM_MEDIUM = PlannerConfiguration.getDiffParameterMedium(); // measured in km
    private final static double DISTANCE_PARAM_HARD = PlannerConfiguration.getDiffParameterHard(); // measured in km

    /**
     * The following parameter is used for the computeDifferenceOfWantedLessons() method.
     */
    private final static double DIFF_WANTED_LESSONS_PARAM = PlannerConfiguration.getDiffWantedLessonsParameter();

    /**
     * The following parameter is the upper bound of the goal interval of the transform() method.
     */
    private final static double TRANSFORMATION_UPPER_BOUND = 100.0;

    /**
     * The following weights are used to compute the score.
     */
    private final static int WEIGHT_DIFF_WANTED_LESSONS = PlannerConfiguration.getWeightDifferentWantedLessons();
    private final static int WEIGHT_DIFF_PREF_CATEGORIES = PlannerConfiguration.getWeightDifferentCategories();
    private final static int WEIGHT_LEVEL = PlannerConfiguration.getWeightLevel();
    private final static int WEIGHT_VEHICLE_CHANGES = PlannerConfiguration.getWeightVehicleChanges();
    private final static int WEIGHT_MULTIPLE_LESSONS = PlannerConfiguration.getWeightMultipleLessons();
    private final static int WEIGHT_DISTANCE = PlannerConfiguration.getWeightDistance();
    private final static int WEIGHT_BLOCKS = PlannerConfiguration.getWeightBlocks();
    //private final static int WEIGHT_IDLE_TIME = PlannerConfiguration.getWeightIdleTime();
    private final static int WEIGHT_OPEN_SLOTS = PlannerConfiguration.getWeightOpenSlots();


    private static double scheduledLessons;

    /**
     * Calculate the score of the given schedule.
     * If the given schedule is infeasible the score must be negative.
     * The higher the scores, the better the schedule and vice versa.
     *
     * @param schedule The schedule to evaluate.
     * @return The associated big decimal score.
     */
    @Override
    public HardSoftScore calculateScore(Schedule schedule) {
        NUMBER_OF_LESSONS = Planner.getNumberOfSlots();
        CONSECUTIVE_LESSONS = Planner.getConsecutiveSlots();
        MAX_DIFF_WANTED_LESSONS = Planner.getMaxDifferenceOfWantedLessons();
        MAX_MULTIPLE_LESSONS = Planner.getMaxStudentsWithMultipleLessons();
        MAX_VEHICLE_CHANGES = Planner.getMaxVehicleChanges();
        MAX_DISTANCE = Planner.getMaxDistanceTransgressions();
        MAX_BLOCKS = Planner.getMaxBlocks();
        //MAX_IDLE_TIME = Planner.getMaxIdleTime();
        MAX_LEVEL = Planner.getMaxLevel();

        int numberExceedingRequirements = scheduleMeetsLegalRequirements(schedule);
        assert(numberExceedingRequirements >= 0);
        if (numberExceedingRequirements > 0)
            return HardSoftScore.ofHard(-1 * numberExceedingRequirements - NUMBER_OF_LESSONS - 1);

        scheduledLessons = computeScheduledSlots(schedule);

        if (scheduledLessons == 0.0)
            return HardSoftScore.ofHard(-1 * NUMBER_OF_LESSONS);

        double hardScore = WEIGHT_DIFF_WANTED_LESSONS * TRANSFORMATION_UPPER_BOUND;
        double softScore = 0.0;

        hardScore -= WEIGHT_DIFF_WANTED_LESSONS * transformedDifferenceOfWantedLessons(schedule);

        softScore -= WEIGHT_DIFF_PREF_CATEGORIES * transformedDifferentPreferenceDrivingCategories(schedule);
        softScore += WEIGHT_LEVEL * transformedOverallLevel(schedule);
        softScore -= WEIGHT_VEHICLE_CHANGES * transformedVehicleChangesOfConsecutiveLessons(schedule);
        softScore -= WEIGHT_MULTIPLE_LESSONS * transformedStudentsWithMultipleLessonsPerDay(schedule);
        softScore -= WEIGHT_DISTANCE * transformedDistanceTransgressionsOfConsecutiveLessons(schedule);
        softScore += WEIGHT_BLOCKS * transformedBlocksOfConsecutiveLessons(schedule);
        //softScore -= WEIGHT_IDLE_TIME * transformedIdleTime(schedule);
        softScore -= WEIGHT_OPEN_SLOTS * transformedOpenSlots();

        return HardSoftScore.of((int) hardScore, (int) softScore);
    }

    /**
     * Transforms a given value of the interval [0, maxValue] into the interval [0, TRANSFORMATION_UPPER_BOUND].
     *
     * @param value The given value.
     * @param maxValue The upper bound of the source interval.
     * @return The transformed value of the goal interval.
     */
    private double transform(double value, double maxValue) {
        if (maxValue == 0.0) return maxValue;
        return (TRANSFORMATION_UPPER_BOUND / maxValue) * value;
    }

    /**
     * Tests if the given schedule meets the legal requirements in terms of the maximal work time per day
     * by giving the number of minutes that exceed the requirements.
     */
    private int scheduleMeetsLegalRequirementsMaximalTime(Schedule schedule) {
        int[] timePerDay = new int[schedule.getDaysToPlan()];
        for (PlanningDrivingLesson lesson : schedule.getAllLessons()) {
            if (lesson.getStudent() != null) {
                PlanningTimeSlot slot = lesson.getSlot();
                int begin = slot.getBegin();
                int end = slot.getEnd();
                int day = slot.getDay();
                if (begin > end) {
                    timePerDay[day] += 24 * 60 - begin;
                    if (day + 1 < timePerDay.length) timePerDay[day + 1] += end;
                } else
                    timePerDay[day] += end - begin;
            }
        }

        int restTime = schedule.getTeacher().getSundayRestTime();
        if (restTime < 0) timePerDay[0] += -1 * restTime;

        int maximumTime;
        if (schedule.getTeacher().isOwner())
            maximumTime = MAXIMAL_TIME_OWNER;
        else
            maximumTime = MAXIMAL_TIME_TEACHER;

        int minutesExceedingRequirements = 0;
        for (int time : timePerDay)
            if (time > maximumTime) minutesExceedingRequirements += time - maximumTime;

        assert(minutesExceedingRequirements >= 0);
        return minutesExceedingRequirements;
    }

    /**
     * Tests if the given schedule meets the legal requirements in terms of the minimal rest time
     * between two days by giving the number of minutes that exceed the requirements.
     *
     * It is not allowed that a driving lesson starts before and ends after 6am!!!
     */
    private int scheduleMeetsLegalRequirementsRestTime(Schedule schedule) {
        Integer[] restTimeToMidnightPerDay = new Integer[schedule.getDaysToPlan()];
        restTimeToMidnightPerDay[0] = schedule.getTeacher().getSundayRestTime();
        Integer[] firstLessonAfter6AMPerDay = new Integer[schedule.getDaysToPlan()];

        for (PlanningDrivingLesson lesson : schedule.getAllLessons()) {
            if (lesson.getStudent() != null) {
                PlanningTimeSlot slot = lesson.getSlot();
                int begin = slot.getBegin();
                int end = slot.getEnd();
                int day = slot.getDay();
                if (begin > end) {
                    if (day + 1 < restTimeToMidnightPerDay.length) {
                        int restTime = -1 * end;
                        if (restTimeToMidnightPerDay[day + 1] == null || restTime < restTimeToMidnightPerDay[day + 1])
                            restTimeToMidnightPerDay[day + 1] = restTime;
                    }
                    if (firstLessonAfter6AMPerDay[day] == null || begin < firstLessonAfter6AMPerDay[day])
                        firstLessonAfter6AMPerDay[day] = begin;
                } else {
                    if (end <= 6 * 60) {
                        int restTime = -1 * end;
                        if (restTimeToMidnightPerDay[day] == null || restTime < restTimeToMidnightPerDay[day])
                            restTimeToMidnightPerDay[day] = restTime;
                        if (day - 1 >= 0) {
                            int firstLesson = 24 * 60 + begin;
                            if (firstLessonAfter6AMPerDay[day - 1] == null || firstLesson < firstLessonAfter6AMPerDay[day - 1])
                                firstLessonAfter6AMPerDay[day - 1] = firstLesson;
                        }
                    } else {
                        assert(begin >= 6 * 60);
                        if (day + 1 < restTimeToMidnightPerDay.length) {
                            int restTime = 24 * 60 - end;
                            if (restTimeToMidnightPerDay[day + 1] == null || restTime < restTimeToMidnightPerDay[day + 1])
                                restTimeToMidnightPerDay[day + 1] = restTime;
                        }
                        if (firstLessonAfter6AMPerDay[day] == null || begin < firstLessonAfter6AMPerDay[day])
                            firstLessonAfter6AMPerDay[day] = begin;
                    }
                }
            }
        }

        int minutesExceedingRequirements = 0;
        for (int i = 0; i < schedule.getDaysToPlan(); ++i) {
            if (firstLessonAfter6AMPerDay[i] != null && restTimeToMidnightPerDay[i] != null) {
                int restTime = firstLessonAfter6AMPerDay[i] + restTimeToMidnightPerDay[i];
                if (restTime < MINIMAl_REST_TIME) minutesExceedingRequirements += MINIMAl_REST_TIME - restTime;
            }
        }

        assert(minutesExceedingRequirements >= 0);
        return minutesExceedingRequirements;
    }

    /**
     * Tests if the given schedule meets the legal requirements by giving the number of minutes that
     * exceed the requirements.
     */
    private int scheduleMeetsLegalRequirements(Schedule schedule) {
        return scheduleMeetsLegalRequirementsMaximalTime(schedule) + scheduleMeetsLegalRequirementsRestTime(schedule);
    }

    /**
     * Computes the number of scheduled slots of the given schedule.
     */
    private int computeScheduledSlots(Schedule schedule) {
        int scheduledSlots = 0;
        for (PlanningDrivingLesson lesson : schedule.getLessons())
            if (lesson.getStudent() != null) ++scheduledSlots;
        return scheduledSlots;
    }

    /**
     * Computes the number of open slots of the given schedule transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedOpenSlots() {
        return transform(NUMBER_OF_LESSONS - scheduledLessons, NUMBER_OF_LESSONS);
    }

    /**
     * Computes the number of different preference driving categories of a lesson
     * and their scheduled student relatively to the number of scheduled slots.
     * This means that the driving category of the scheduled student is not in the
     * preference list of the lesson.
     */
    private double computeDifferentPreferenceDrivingCategories(Schedule schedule) {
        int difference = 0;
        for (PlanningDrivingLesson lesson : schedule.getLessons()) {
            PlanningStudent student = lesson.getStudent();
            if (student != null)
                if (!(lesson.getSlot().categoryIsPreference(student.getCategory())))
                    ++difference;
        }
        return difference / scheduledLessons;
    }

    /**
     * Computes the number of different preference driving categories of a lesson and their scheduled student
     *  relatively to the number of scheduled slots transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedDifferentPreferenceDrivingCategories(Schedule schedule) {
        return transform(computeDifferentPreferenceDrivingCategories(schedule), 1.0);
    }

    private int computeLessonsPerStudent(Schedule schedule, PlanningStudent student) {
        int lessonsOfStudent = 0;
        for (PlanningDrivingLesson lesson : schedule.getLessons())
            if (student.equals(lesson.getStudent())) ++lessonsOfStudent;
        return lessonsOfStudent;
    }

    private int computeDifferenceOfWantedLessonsPerStudent(Schedule schedule, PlanningStudent student) {
        return Math.abs(computeLessonsPerStudent(schedule, student) - student.getWantedLessons());
    }

    /**
     * Computes the difference of wanted lessons to the scheduled lessons relatively to the number of scheduled slots.
     * The score is computed similar to the Gaussian-Distribution with parameter DIFF_WANTED_LESSONS_PARAM.
     * The higher the difference, the worse the schedule.
     */
    private double computeDifferenceOfWantedLessons(Schedule schedule) {
        double difference = 0.0;
        for (PlanningStudent student : schedule.getStudents()) {
            int differencePerStudent = computeDifferenceOfWantedLessonsPerStudent(schedule, student);
            if (differencePerStudent != 0)
                difference += Math.pow(differencePerStudent, DIFF_WANTED_LESSONS_PARAM);
        }
        return difference / scheduledLessons;
    }

    /**
     * Computes the difference of wanted lessons to the scheduled lessons relatively to the number
     * of scheduled slots transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedDifferenceOfWantedLessons(Schedule schedule) {
        return transform(computeDifferenceOfWantedLessons(schedule), MAX_DIFF_WANTED_LESSONS / scheduledLessons);
    }

    private int computeDaysWithMultipleLessonsPerStudent(Schedule schedule, PlanningStudent student) {
        int[] lessonsPerDay = new int[schedule.getDaysToPlan()];
        for (PlanningDrivingLesson lesson : schedule.getAllLessons()) {
            if (student.equals(lesson.getStudent())) {
                PlanningTimeSlot slot = lesson.getSlot();
                int day = slot.getDay();
                if (slot.getEnd() <= 6 * 60 && slot.getBegin() <= slot.getEnd())
                    if (day - 1 >= 0) ++lessonsPerDay[day - 1];
                else
                    ++lessonsPerDay[day];
            }
        }
        int daysWithMultipleLessons = 0;
        for (int numberOfLessons : lessonsPerDay)
            if (numberOfLessons >= 2) ++daysWithMultipleLessons;
        return daysWithMultipleLessons;
    }

    /**
     * Computes the number of days and students with multiple lessons at one day relatively to the number of scheduled slots.
     * E.g. if one student has three days with more than one lesson, this increments the returned value by 3.
     */
    private double computeStudentsWithMultipleLessonsPerDay(Schedule schedule) {
        int studentsWithMultipleLessonsPerDay = 0;
        for (PlanningStudent student : schedule.getStudents())
            studentsWithMultipleLessonsPerDay += computeDaysWithMultipleLessonsPerStudent(schedule, student);
        return studentsWithMultipleLessonsPerDay / scheduledLessons;
    }

    /**
     * Computes the number of days and students with multiple lessons at one day relatively to the number of
     * scheduled slots transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedStudentsWithMultipleLessonsPerDay(Schedule schedule) {
        return transform(computeStudentsWithMultipleLessonsPerDay(schedule), MAX_MULTIPLE_LESSONS / scheduledLessons);
    }

    /**
     * Computes the number of necessary vehicle changes  relatively to the number of scheduled slots
     * because two consecutive lessons need different vehicles.
     */
    private double computeVehicleChangesOfConsecutiveLessons(Schedule schedule) {
        int vehicleChanges = 0;
        for (List<Long> consecutiveLessonList : CONSECUTIVE_LESSONS) {
            PlanningDrivingLesson currentLesson = schedule.getDrivingLessonById(consecutiveLessonList.get(0));
            assert(currentLesson != null);
            for (int i = 1; i < consecutiveLessonList.size(); ++i) {
                PlanningDrivingLesson nextLesson = schedule.getDrivingLessonById(consecutiveLessonList.get(i));
                assert(nextLesson != null);
                PlanningStudent currentStudent = currentLesson.getStudent();
                PlanningStudent nextStudent = nextLesson.getStudent();
                if (currentStudent != null && nextStudent != null) {
                    if (!(currentStudent.getCategory().isCompatibleWith(nextStudent.getCategory())))
                        ++vehicleChanges;
                }
                currentLesson = nextLesson;
            }
        }
        return vehicleChanges / scheduledLessons;
    }

    /**
     * Computes the number of necessary vehicle changes transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedVehicleChangesOfConsecutiveLessons(Schedule schedule) {
        return transform(computeVehicleChangesOfConsecutiveLessons(schedule), MAX_VEHICLE_CHANGES / scheduledLessons);
    }

    /**
     * Computes the number of distance transgressions according to the three limits between every destination
     * and the next pickup location of consecutive lessons relatively to the number of scheduled slots.
     * Currently, the computation is based on the beeline.
     */
    private double computeDistanceTransgressionsOfConsecutiveLessons(Schedule schedule) {
        int distanceTransgressions = 0;
        for (List<Long> consecutiveLessonList : CONSECUTIVE_LESSONS) {
            PlanningDrivingLesson currentLesson = schedule.getDrivingLessonById(consecutiveLessonList.get(0));
            assert(currentLesson != null);
            for (int i = 1; i < consecutiveLessonList.size(); ++i) {
                PlanningDrivingLesson nextLesson = schedule.getDrivingLessonById(consecutiveLessonList.get(i));
                assert(nextLesson != null);
                PlanningStudent currentStudent = currentLesson.getStudent();
                PlanningStudent nextStudent = nextLesson.getStudent();
                if (currentStudent != null && nextStudent != null) {
                    PlanningLocation destination = currentStudent.getBelongingPreference(currentLesson.getSlot()).getDestination();
                    PlanningLocation pickup = nextStudent.getBelongingPreference(nextLesson.getSlot()).getPickup();
                    double distance = destination.distanceTo(pickup);
                    if (distance > DISTANCE_PARAM_HARD)
                        distanceTransgressions += 3;
                    else
                        if (distance > DISTANCE_PARAM_MEDIUM)
                            distanceTransgressions += 2;
                        else
                            if (distance > DISTANCE_PARAM_SOFT)
                                ++distanceTransgressions;
                }
                currentLesson = nextLesson;
            }
        }
        return distanceTransgressions / scheduledLessons;
    }

    /**
     * Computes the number of distance transgressions according to the three limits between every destination
     * and the next pickup location of consecutive lessons relatively to the number of scheduled slots
     * transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedDistanceTransgressionsOfConsecutiveLessons(Schedule schedule) {
        return transform(computeDistanceTransgressionsOfConsecutiveLessons(schedule), MAX_DISTANCE / scheduledLessons);
    }

    private double computeBlocksOfConsecutiveLessons(Schedule schedule) {
        int numberOfBlocks = 0;
        for (List<Long> consecutiveLessonList : CONSECUTIVE_LESSONS) {
            PlanningDrivingLesson currentLesson = schedule.getDrivingLessonById(consecutiveLessonList.get(0));
            assert(currentLesson != null);
            for (int i = 1; i < consecutiveLessonList.size(); ++i) {
                PlanningDrivingLesson nextLesson = schedule.getDrivingLessonById(consecutiveLessonList.get(i));
                assert(nextLesson != null);
                if (currentLesson.getStudent() != null && nextLesson.getStudent() != null)
                    ++numberOfBlocks;
                currentLesson = nextLesson;
            }
        }
        return numberOfBlocks / scheduledLessons;
    }

    private double transformedBlocksOfConsecutiveLessons(Schedule schedule) {
        return transform(computeBlocksOfConsecutiveLessons(schedule), MAX_BLOCKS);
    }

    /**
     * Computes the overall idle time relatively to the number of scheduled slots.
     * Idle time means the number of unassigned slots between two assigned slots at the same day.
     */
    /*private double computeIdleTime(Schedule schedule) {
        List<List<Boolean>> slotsAreOccupiedPerDay = new ArrayList<>();
        for (int i = 0; i < schedule.getDaysToPlan(); ++i)
            slotsAreOccupiedPerDay.add(new ArrayList<>());
        for (PlanningDrivingLesson lesson : schedule.getAllLessons())
            slotsAreOccupiedPerDay.get(lesson.getSlot().getDay()).add(lesson.getStudent() != null);
        int idleTime = 0;
        for (List<Boolean> slotsAreOccupiedOneDay : slotsAreOccupiedPerDay) {
            int possibleIdleTime = 0;
            boolean idleTimeIsPossible = false;
            for (Boolean slotIsOccupied : slotsAreOccupiedOneDay) {
                if (slotIsOccupied) {
                    if (idleTimeIsPossible) {
                        idleTime += possibleIdleTime;
                        possibleIdleTime = 0;
                    } else
                        idleTimeIsPossible = true;
                } else
                    if (idleTimeIsPossible) ++possibleIdleTime;
            }
        }
        return idleTime / scheduledLessons;
    }*/

    /**
     * Computes the overall idle time relatively to the number of scheduled slots
     * transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    /*private double transformedIdleTime(Schedule schedule) {
        return transform(computeIdleTime(schedule), MAX_IDLE_TIME / scheduledLessons);
    }*/

    /**
     * Computes the overall level of the students of all scheduled lessons relatively to the number of scheduled slots.
     */
    private double computeOverallLevel(Schedule schedule) {
        int overallLevel = 0;
        for (PlanningDrivingLesson lesson : schedule.getLessons()) {
            PlanningStudent student = lesson.getStudent();
            if (student != null) overallLevel += student.getLevel();
        }
        return overallLevel / scheduledLessons;
    }

    /**
     * Computes the overall level of the students of all scheduled lessons relatively to the number of scheduled slots
     * transformed in the interval [0, TRANSFORMATION_UPPER_BOUND].
     */
    private double transformedOverallLevel(Schedule schedule) {
        return transform(computeOverallLevel(schedule), MAX_LEVEL * scheduledLessons);
    }

}
