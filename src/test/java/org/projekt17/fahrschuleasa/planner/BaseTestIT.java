package org.projekt17.fahrschuleasa.planner;

import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BaseTestIT {

    private ConstructionHelper constructionHelper;

    private PlanningTeacher planningTeacher;
    private PlanningLocation pickupLocation;
    private PlanningLocation destinationLocation;

    public enum TestConfig {
        LESSON_LENGTH(90);

        private final int value;

        TestConfig(int value) {
            this.value = value;
        }

        private int getValue() {
            return value;
        }
    }

    String printWeekDay(int day) {
        switch (day) {
            case 0:
                return "monday";
            case 1:
                return "tuesday";
            case 2:
                return "wednesday";
            case 3:
                return "thursday";
            case 4:
                return "friday";
            case 5:
                return "saturday";
            case 6:
                return "sunday";
            default:
                return "invalid week day";
        }
    }

    public static int dayTime(int hour, int minute) {
        return hour * 60 + minute;
    }

    List<Integer> wholeWeek = Stream.of(0, 1, 2, 3, 4, 5, 6).collect(Collectors.toList());
    List<Integer> onlyMonday = Stream.of(0).collect(Collectors.toList());


    public void init(int start, int slotsPerDay, int wantedLessons, List<Integer> weekdays) {

         constructionHelper = new ConstructionHelper();

        pickupLocation = new PlanningLocation(49.2577, 7.0450);
        destinationLocation = new PlanningLocation(49.3383, 6.9285);
        List<PlanningTimeSlot> slotList = new ArrayList<>();
        List<PlanningStudent> studentList = new ArrayList<>();
        List<DrivingCategory> categoryList = new ArrayList<>();
        categoryList.add(DrivingCategory.B);
        List<DrivingCategory> optionalCategoryList = new ArrayList<>();
        optionalCategoryList.add(DrivingCategory.A);

        planningTeacher = new PlanningTeacher((long)0, true, slotList, new ArrayList<>(), studentList,
            true, false, dayTime(11, 0));

        int j = 0;
        for (int day : weekdays) {
            int current = start;
            for (int i = 0; i < slotsPerDay; i++) {
                int next = current + TestConfig.LESSON_LENGTH.getValue();
                studentList.add(new PlanningStudent((long) j, true, DrivingCategory.B,
                    7, planningTeacher, wantedLessons, new ArrayList<>(), true, new ArrayList<>()));
                slotList.add(new PlanningTimeSlot((long)j++, current, next,
                    day, categoryList, optionalCategoryList));
                current = next;
            }
        }
        planningTeacher.setSlots(slotList);
        planningTeacher.setStudents(studentList);
    }


    /**
     * Each student has selected all time Slots as preferences.
     * Tests if every time slot is assigned to a student (i.e. the assigned time slot is not null).
     */
    @Test
    void freeAssignment() {
        init(dayTime(8, 0), 5, 1, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();


        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            assertNotNull(drivingLesson.getStudent(),
                drivingLesson.getSlot().toString() +
                    " was not assigned \n even though there are enough students and an easy assignment would be allowed");
        }
    }

    /**
     * Each student has selected only one time slot as preference.
     * The id of the student and the id of the time slot match.
     * Tests if every student is assigned their preference.
     */
    @Test
    void definiteAssignment() {
        init(dayTime(8, 0), 5, 1, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();
        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            assertNotNull(drivingLesson.getStudent(), "There is one student for every slot");
            assertEquals(drivingLesson.getId(), drivingLesson.getStudent().getId(),
                "Every student picked only one preference. \n" + "" +
                    "The student id and the time slot id must match. Mismatch for \n" +
                    drivingLesson.getSlot().toString());
        }
    }

    /**
     * Tests if a non-active student (here student 2) is excluded.
     * Since every student has only selected ony preference the respective slot must remain empty.
     */
    @Test
    void nonactiveStudent0() {
        init(dayTime(8, 0), 1, 1, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {

            if (student.getId() == 2) {
                student.setActive(false);
                continue;
            }
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                if (student.getId() == slot.getId() && student.getId() != 2) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();
        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if ((int)drivingLesson.getId() != 2) {
                assertNotNull(drivingLesson.getStudent(),"There are students for every slot except for slot 2");
                assertEquals(drivingLesson.getId(), drivingLesson.getStudent().getId(),
                    "Every student picked only one preference. \n" + "" +
                        "The student id and the time slot id must match. Mismatch for \n" +
                        drivingLesson.getSlot().toString());
            } else assertNull(drivingLesson.getStudent(), "There is no student with preferences for time slot 2");
        }
    }

    /**
     * Tests if a non-active student (here student 2) is excluded.
     * Since every other student has selected every possible slot as preference the slot must be assigned nonetheless.
     */
    @Test
    void nonactiveStudent1() {
        init(dayTime(8, 0), 1, 2, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {

            if ((int) student.getId() == 2) {
                student.setActive(false);
                continue;
            }
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();
        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            assertNotNull(drivingLesson.getStudent(),
                "Every time slot must be assigned");
            assertNotEquals(2, (int) drivingLesson.getStudent().getId(),
                "Student 2 is not active and must not be assigned to \n" +
                    drivingLesson.getSlot().toString());
        }
    }

    /**
     * An overly eager teacher has selected all possible 90 minute slots of each weak day.
     * The students are equally mad and selected everything as preference.
     * Test if the total number of assigned lessons is not higher than 35 (the maximal allowed workload)
     */
    @Test
    void hardcoreDrivingSchool0() {
        init(dayTime(0, 0), 16, 2, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();
        int counter = 0;
        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() != null) counter++;
        }
        if (counter > 35) fail("You scheduled " + counter + " lessons a weak. That's definitely too many lessons.");
    }

    /**
     * An overly eager teacher has selected all possible 90 minute slots of each weak day.
     * The students are equally mad and selected everything as preference.
     * Test if the total number of assigned lessons is exactly 35.
     * There must be as many lessons as possible in those circumstances.
     */
    @Test
    void hardcoreDrivingSchool1() {
        init(dayTime(0, 0), 16, 2, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();
        int counter = 0;
        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() != null) counter++;
        }
        if (counter != 35) fail("You scheduled " + counter + " lessons a weak. Exactly 35 are possible");
    }

    /**
     * A teacher has enough students to fill all slots a week.
     * He should not sleep in the car though (i.e. there must be no driving lesson for at least 11 hours).
     */
    @Test
    void teachersRest() {
        init(dayTime(0, 0), 16, 1, wholeWeek);
        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        int[] earliest = new int[7];
        int[] last = new int[7];

        for (int i = 0; i < 7; i++) {
            last[i] = 24*60*i+6*60;
            earliest[i] = 24*60*(i+1)+6*60;
        }

        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            int day = drivingLesson.getSlot().getDay();
            if (drivingLesson.getStudent() == null) continue;

            int begin = drivingLesson.getSlot().getBegin();
            int end = drivingLesson.getSlot().getEnd();

            if (begin<=270){
                if (day==0) continue;
                earliest[day-1] = Math.min(begin+24*60*day, earliest[day-1]);
                last[day-1] = Math.max(end+24*60*day, last[day-1]);
            } else {
            earliest[day] = Math.min(begin+24*60*day, earliest[day]);
            last[day] = Math.max(end+24*60*day, last[day]);}
        }

        for (int i = 0; i < 6; i++) {
            int gap = earliest[i + 1]- last[i];
            if (gap < 660) fail("There must be at least an 660 minute gap. \n" +
                "You chose a gap of " + gap + " minutes between " + printWeekDay(i) + " and " + printWeekDay(i + 1) + ".");
        }
    }

    /**
     * Check initial rest time with a sunday rest of 0 hours.
     */
    @Test
    void teachersRestInitial0() {
        List<Integer> onlyMonday = Stream.of(0).collect(Collectors.toList());
        init(dayTime(6, 0), 12, 1, onlyMonday);

        planningTeacher.setSundayRestTime(0);

        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        int earliest = (dayTime(24, 0));

        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() == null) continue;
            earliest = Math.min(drivingLesson.getSlot().getBegin(), earliest);
        }

        assertTrue(earliest < dayTime(24,0), "The planner has to schedule at least one lesson to a student.");
        if (earliest < 660) fail("There must be at least an 660 minute gap. \n" +
            "You chose a gap of " + earliest + " minutes between sunday and monday.");
    }

    /**
     * Check initial rest time with a sunday rest of 3 hours.
     */
    @Test
    void teachersRestInitial1() {
        init(dayTime(6, 0), 12, 1, onlyMonday);

        planningTeacher.setSundayRestTime(180);

        for (PlanningStudent student : planningTeacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : planningTeacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        int earliest = (dayTime(24, 0));

        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() == null) continue;
            earliest = Math.min(drivingLesson.getSlot().getBegin(), earliest);
        }

        assertTrue(earliest < dayTime(24,0), "The planner has to schedule at least one lesson to a student.");
        if (earliest + 180 < 660) fail("There must be at least an 660 minute gap. \n" +
            "You chose a gap of " + (earliest + 180) + " minutes between sunday and monday.");
    }

    /**
     * Check if the solution is flagged feasible for a simple assignment of one time slot to one student.
     */
    @Test
    void triviallyFeasible() {
        List<Integer> onlyWednesday = Stream.of(2).collect(Collectors.toList());
        init(dayTime(14, 0), 1, 1, onlyWednesday);

        PlanningStudent student = planningTeacher.getStudents().get(0);
        PlanningTimeSlot slot = planningTeacher.getSlots().get(0);
        student.setPreferences(new ArrayList<>(Stream.of(
            new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student)).
            collect(Collectors.toList())));

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);

        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        assertTrue(solution.getScore().isFeasible(), "This planning task must be feasible");
    }

    /**
     * Check if the planner is capable of assigning one time slot to one student
     */
    @Test
    void trivialAssignment() {
        List<Integer> onlyWednesday = Stream.of(2).collect(Collectors.toList());
        init(dayTime(14, 0), 1, 1, onlyWednesday);

        PlanningStudent student = planningTeacher.getStudents().get(0);
        PlanningTimeSlot slot = planningTeacher.getSlots().get(0);
        student.setPreferences(new ArrayList<>(Stream.of(
            new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student)).
            collect(Collectors.toList())));

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);

        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        assertNotNull(solution.getLessons().get(0).getStudent() ,"The planner should be capable of assigning a " +
            "single slot to a singe student");
    }

    /**
     * There is only one time slot and the student has selected no preferences. Score of solution must be unfeasible.
     */
    @Test
    void triviallyUnfeasible() {
        List<Integer> onlyWednesday = Stream.of(2).collect(Collectors.toList());
        init(dayTime(14, 0), 1, 1, onlyWednesday);

        Teacher teacher = constructionHelper.createTeacherFromPlanning(planningTeacher);
        Planner planner = new Planner(teacher, LocalDate.ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        assertFalse(solution.getScore().isFeasible(), "This planning task is not feasible.\n" +
            "There is no student who selected any preferences.");
    }

}
