package org.projekt17.fahrschuleasa.planner;

import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class BaseTest {

    private PlanningTeacher teacher;
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
        pickupLocation = new PlanningLocation(49.2577, 7.0450);
        destinationLocation = new PlanningLocation(49.3383, 6.9285);
        //destinationLocation = pickupLocation;
        List<PlanningTimeSlot> slotList = new ArrayList<>();
        List<PlanningStudent> studentList = new ArrayList<>();
        List<DrivingCategory> categoryList = new ArrayList<>();
        categoryList.add(DrivingCategory.B);
        List<DrivingCategory> optionalCategoryList = new ArrayList<>();
        optionalCategoryList.add(DrivingCategory.A);

        teacher = new PlanningTeacher((long)0, true, slotList, new ArrayList<>(),studentList,
            true, false, dayTime(11, 0));

        int j = 0;
        for (int day : weekdays) {
            int current = start;
            for (int i = 0; i < slotsPerDay; i++) {
                int next = current + TestConfig.LESSON_LENGTH.getValue();
                studentList.add(new PlanningStudent((long) j, true, DrivingCategory.B,
                    7, teacher, wantedLessons, new ArrayList<>(), true, new ArrayList<>()));
                slotList.add(new PlanningTimeSlot(j++, current, next,
                    day, categoryList, optionalCategoryList));
                current = next;
            }
        }
        teacher.setSlots(slotList);
        teacher.setStudents(studentList);
    }


    /**
     * Each student has selected all time Slots as preferences.
     * Tests if every time slot is assigned to a student (i.e. the assigned time slot is not null).
     */
    @Test
    void freeAssignment() {
        init(dayTime(8, 0), 5, 1, wholeWeek);
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
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
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
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
        for (PlanningStudent student : teacher.getStudents()) {

            if (student.getId() == 2) {
                student.setActive(false);
                continue;
            }
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                if (student.getId() == slot.getId() && student.getId() != 2) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
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
        for (PlanningStudent student : teacher.getStudents()) {

            if ((int) student.getId() == 2) {
                student.setActive(false);
                continue;
            }
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
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
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
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
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();
        int counter = 0;
        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() != null) counter++;
        }
        if (counter != 35) fail("You scheduled " + counter + " lessons a weak. Exactly 35 are possible");
    }

    /**
     * A teacher has selected 8 possible 90 minute slots for Monday.
     * After the week all students decided to keep their slots.
     * Therefore both schedules must coincide.
     */
    @Test
    void ConsecutiveWeekTest0() {
        init(dayTime(8, 0), 8, 1, onlyMonday);
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher);
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (PlanningStudent student : teacher.getStudents()){
            student.setPreferencesChanged(false);
        }

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher);
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        for (PlanningDrivingLesson lesson1 : firstSchedule.getLessons()){
            for (PlanningDrivingLesson lesson2 : secondSchedule.getLessons()){
                if (lesson1.getSlot().getId()==lesson2.getSlot().getId()){
                    if ((null==lesson1.getStudent()&&null!=lesson2.getStudent())||
                        (null==lesson2.getStudent()&&null!=lesson1.getStudent()))
                        fail("The schedules must be identical.");
                    if (null==lesson1.getStudent()) continue;
                    assertEquals(lesson1.getStudent().getId(),lesson2.getStudent().getId(),
                        "The schedules must be identical");
                }
            }
        }
    }

    /**
     * A teacher has selected 8 possible 90 minute slots for Monday.
     * The students selected everything as preference.
     * After the week all students decided to keep their slots.
     * Additionally all students that were unsuccessful for week 1 have set "preferences changed" to true.
     * The legal requirements must still be kept. So no more than 5 lessons may be assigned.
     */
    @Test
    void ConsecutiveWeekTest1() {
        init(dayTime(8, 0), 8, 1, onlyMonday);
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher);
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (PlanningStudent student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent().getId()==student.getId()) assigned=true;
            }
            student.setPreferencesChanged(!assigned);
        }

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher);
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        int counter=0;
        for (PlanningDrivingLesson lesson : secondSchedule.getLessons()){
            if (lesson.getStudent()!=null) counter++;
        }

        if (counter>5) fail("You assigned too many lesson. 5 would be allowed. You assigned "+counter);
    }

    /**
     * A teacher has selected 8 possible 90 minute slots for Monday.
     * The students selected everything as preference.
     * After the week all students decided to keep their slots except for student 0 who is now inactive.
     * Additionally all students that were unsuccessful for week 1 have preference changed to true.
     * The legal requirements must still be kept. So no more than 5 lessons may be assigned.
     */
    @Test
    void ConsecutiveWeekTest2() {
        init(dayTime(8, 0), 8, 1, onlyMonday);
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher);
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (PlanningStudent student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent().getId()==student.getId()) assigned=true;
            }
            student.setPreferencesChanged(!assigned);
            if (student.getId() == 0) student.setActive(false);
        }

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher);
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        int counter=0;
        for (PlanningDrivingLesson lesson : secondSchedule.getLessons()){
            if (lesson.getStudent()!=null) {
                counter++;
            }
        }

        if (counter>5) fail("You assigned too many lesson. 5 would be allowed. You assigned "+counter);
    }

    /**
     * A teacher has selected 8 possible 90 minute slots for Monday.
     * The students selected everything as preference.
     * After the week all students decided to keep their slots except for one student who is now inactive.
     * Additionally all students that were unsuccessful for week 1 have preference changed to true.
     * The remaining 4 students must keep their slots.
     */
    @Test
    void ConsecutiveWeekTest3() {
        init(dayTime(8, 0), 8, 1, onlyMonday);
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                preferences.add(new PlanningPreference(slot.getId(), slot, pickupLocation, destinationLocation, student));
            }
            student.setPreferences(preferences);
        }

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher);
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        boolean first=true;

        for (PlanningStudent student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent()==student) {assigned=true;
                if (first){
                    student.setActive(false);
                    first=false;
                }
                }
            }
            student.setPreferencesChanged(!assigned);
        }

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher);
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        for (PlanningDrivingLesson newLesson : secondSchedule.getLessons()){
            if (newLesson.getStudent()==null) continue;

            for(PlanningDrivingLesson oldLesson : firstSchedule.getLessons()){
                if (oldLesson.getStudent()==null ) continue;
                if (oldLesson.getStudent().equals(newLesson.getStudent())){
                    assertEquals(newLesson.getId(),oldLesson.getId());
                }
            }
        }
    }

    /**
     * A teacher has enough students to fill all slots a week.
     * He should not sleep in the car though (i.e. there must be no driving lesson for at least 11 hours).
     */
    @Test
    void teachersRest() {
        init(dayTime(6, 0), 12, 1, wholeWeek);
        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int[] earliest = new int[7];
        int[] last = new int[7];

        for (int i = 0; i < 7; i++) {
            last[i] = (dayTime(0, 0));
            earliest[i] = (dayTime(24, 0));
        }

        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            int day = drivingLesson.getSlot().getDay();
            if (drivingLesson.getStudent() == null) continue;
            earliest[day] = Math.min(drivingLesson.getSlot().getBegin(), earliest[day]);
            last[day] = Math.max(drivingLesson.getSlot().getEnd(), last[day]);
        }

        for (int i = 0; i < 6; i++) {
            int gap = dayTime(24, 0) - last[i] + earliest[i + 1];
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

        teacher.setSundayRestTime(0);

        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int earliest = (dayTime(24, 0));

        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() == null) continue;
            earliest = Math.min(drivingLesson.getSlot().getBegin(), earliest);
        }

        assertTrue(earliest < dayTime(24,0), "The planner has to schedule at least one lesson to a student.");
        int gap = earliest;
        if (gap < 660) fail("There must be at least an 660 minute gap. \n" +
            "You chose a gap of " + gap + " minutes between sunday and monday.");
    }

    /**
     * Check initial rest time with a sunday rest of 3 hours.
     */
    @Test
    void teachersRestInitial1() {
        init(dayTime(6, 0), 12, 1, onlyMonday);

        teacher.setSundayRestTime(180);

        for (PlanningStudent student : teacher.getStudents()) {
            List<PlanningPreference> preferences = new ArrayList<>();
            for (PlanningTimeSlot slot : teacher.getSlots()) {
                if (student.getId() == slot.getId()) {
                    preferences.add(new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student));
                }
            }
            student.setPreferences(preferences);
        }
        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int earliest = (dayTime(24, 0));

        for (PlanningDrivingLesson drivingLesson : solution.getLessons()) {
            if (drivingLesson.getStudent() == null) continue;
            earliest = Math.min(drivingLesson.getSlot().getBegin(), earliest);
        }

        assertTrue(earliest < dayTime(24,0), "The planner has to schedule at least one lesson to a student.");
        int gap = 180 + earliest;
        if (gap < 660) fail("There must be at least an 660 minute gap. \n" +
            "You chose a gap of " + gap + " minutes between sunday and monday.");
    }

    /**
     * Check if the solution is flagged feasible for a simple assignment of one time slot to one student.
     */
    @Test
    void triviallyFeasible() {
        List<Integer> onlyWednesday = Stream.of(2).collect(Collectors.toList());
        init(dayTime(14, 0), 1, 1, onlyWednesday);

        PlanningStudent student = teacher.getStudents().get(0);
        PlanningTimeSlot slot = teacher.getSlots().get(0);
        student.setPreferences(new ArrayList<>(Stream.of(
            new PlanningPreference(student.getId(), slot, pickupLocation, destinationLocation, student)).
            collect(Collectors.toList())));

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertTrue(solution.getScore().isFeasible(), "This planning task must be feasible");
    }

    /**
     * There is only one time slot and the student has selected no preferences. Score of solution must be unfeasible.
     */
    @Test
    void triviallyUnfeasible() {
        List<Integer> onlyWednesday = Stream.of(2).collect(Collectors.toList());
        init(dayTime(14, 0), 1, 1, onlyWednesday);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertFalse(solution.getScore().isFeasible(), "This planning task is not feasible.\n" +
            "There is no student who selected any preferences.");
    }

}
