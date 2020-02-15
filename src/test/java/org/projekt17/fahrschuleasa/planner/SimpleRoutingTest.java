package org.projekt17.fahrschuleasa.planner;

import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.projekt17.fahrschuleasa.planner.BaseTest.dayTime;

public class SimpleRoutingTest {

    private PlanningTeacher teacher;

    PlanningLocation location0 = new PlanningLocation(48.2577, 7.0450);
    PlanningLocation location1 = new PlanningLocation(49.3577, 7.0450);
    PlanningLocation location2 = new PlanningLocation(50.4577, 7.0450);




    /**
     * Initialize routing tests for a single day.
     * Apart from their id and their pickup and destination location (and their id) all students are equal.
     * For each slot a student is created with their id matching the slot id.
     *
     * @param slots The of consecutive slots to be created (only 16 are supported).
     */
    public void init(int slots) {
        List<PlanningTimeSlot> slotList = new ArrayList<>();
        List<PlanningStudent> studentList = new ArrayList<>();
        List<DrivingCategory> categoryList = new ArrayList<>();
        categoryList.add(DrivingCategory.B);
        List<DrivingCategory> optionalCategoryList = new ArrayList<>();
        optionalCategoryList.add(DrivingCategory.A);

        teacher = new PlanningTeacher(0, true, slotList, new ArrayList<>(),studentList,
            true, false, dayTime(11, 0));

        int j = 0;
        int current = dayTime(0, 0);
        for (int i = 0; i < slots; i++) {
            int next = current + 90;
            studentList.add(new PlanningStudent((long) j, true, DrivingCategory.B,
                7, teacher, 1, new ArrayList<>(), true, new ArrayList<>()));
            slotList.add(new PlanningTimeSlot(j++, current, next,
                0, categoryList, optionalCategoryList));
            current = next;
        }

        teacher.setSlots(slotList);
        teacher.setStudents(studentList);
    }

    /**
     * Student 0 has selected location 0 both as pickup an as destination location.
     * Student 1 has selected location 1 as pickup and location 0 as destination location.
     * Test if student 0 is put after student 1.
     */
    @Test
    void simpleRouting0() {
        init(2);

        PlanningStudent student0 = teacher.getStudents().get(0);
        PlanningStudent student1 = teacher.getStudents().get(1);


        PlanningPreference preference00 =
            new PlanningPreference(0, teacher.getSlots().get(0), location0, location0, student0);
        PlanningPreference preference01 =
            new PlanningPreference(1, teacher.getSlots().get(1), location0, location0, student0);
        student0.setPreferences(new ArrayList<>(Stream.of(preference00,preference01).
            collect(Collectors.toList())));

        PlanningPreference preference10 =
            new PlanningPreference(2, teacher.getSlots().get(0), location1, location0, student1);
        PlanningPreference preference11 =
            new PlanningPreference(3, teacher.getSlots().get(1), location1, location0, student1);
        student1.setPreferences(new ArrayList<>(Stream.of(preference10,preference11).
            collect(Collectors.toList())));

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            assertNotNull(lesson.getStudent(), "There are enough students for all slots");
        }

        assertEquals(1,(int)solution.getLessons().get(0).getStudent().getId(),
            "The first time slot must be assigned to student 1");

        assertEquals(0,(int)solution.getLessons().get(1).getStudent().getId(),
            "The second time slot must be assigned to student 0");

    }

    /**
     * Student 1 and Student 2 have selected location 0 as pickup and destination location.
     * Student 0 has chosen location 1 as pickup and location 2 as destination location.
     * Check that student 0 is not scheduled between student 1 and student 2.
     */
    @Test
    void simpleRouting1() {
        init(3);

        PlanningStudent student0 = teacher.getStudents().get(0);
        PlanningStudent student1 = teacher.getStudents().get(1);
        PlanningStudent student2 = teacher.getStudents().get(2);


        PlanningPreference preference00 =
            new PlanningPreference(0, teacher.getSlots().get(0), location1, location2, student0);
        PlanningPreference preference01 =
            new PlanningPreference(1, teacher.getSlots().get(1), location1, location2, student0);
        PlanningPreference preference02 =
            new PlanningPreference(2, teacher.getSlots().get(2), location1, location2, student0);
        student0.setPreferences(new ArrayList<>(Stream.of(preference00,preference01,preference02).
            collect(Collectors.toList())));

        PlanningPreference preference10 =
            new PlanningPreference(3, teacher.getSlots().get(0), location0, location0, student1);
        PlanningPreference preference11 =
            new PlanningPreference(4, teacher.getSlots().get(1), location0, location0, student1);
        PlanningPreference preference12 =
            new PlanningPreference(5, teacher.getSlots().get(2), location0, location0, student1);

        student1.setPreferences(new ArrayList<>(Stream.of(preference10,preference11,preference12).
            collect(Collectors.toList())));

        PlanningPreference preference20 =
            new PlanningPreference(6, teacher.getSlots().get(0), location0, location0, student2);
        PlanningPreference preference21 =
            new PlanningPreference(7, teacher.getSlots().get(1), location0, location0, student2);
        PlanningPreference preference22 =
            new PlanningPreference(8, teacher.getSlots().get(2), location0, location0, student2);
        student2.setPreferences(new ArrayList<>(Stream.of(preference20,preference21,preference22).
            collect(Collectors.toList())));

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            assertNotNull(lesson.getStudent(), "There are enough students for all slots");
        }

        for (PlanningDrivingLesson lesson : solution.getLessons()) {
            if (lesson.getSlot().getId() == 1) {
                assertNotEquals(0,(int)lesson.getStudent().getId(),"Slot 1 must not be assigned to student 0.\n " +
                    "Student 1 and student 2 must have consecutive lessons");
                break;
            }
        }
    }

    /**
     * Student 0 and student 1 have both selected location 1 as pickup and location 2 as destination location.
     * Student 2 has chosen location 2 as pickup and location 1 as destination location.
     * Test if student 2 is put in the middle.
     */
    @Test
    void simpleRouting2() {
        init(3);

        PlanningStudent student0 = teacher.getStudents().get(0);
        PlanningStudent student1 = teacher.getStudents().get(1);
        PlanningStudent student2 = teacher.getStudents().get(2);


        PlanningPreference preference00 =
            new PlanningPreference(0, teacher.getSlots().get(0), location1, location2, student0);
        PlanningPreference preference01 =
            new PlanningPreference(1, teacher.getSlots().get(1), location1, location2, student0);
        PlanningPreference preference02 =
            new PlanningPreference(2, teacher.getSlots().get(2), location1, location2, student0);
        student0.setPreferences(new ArrayList<>(Stream.of(preference00,preference01,preference02).
            collect(Collectors.toList())));

        PlanningPreference preference10 =
            new PlanningPreference(3, teacher.getSlots().get(0), location1, location2, student1);
        PlanningPreference preference11 =
            new PlanningPreference(4, teacher.getSlots().get(1), location1, location2, student1);
        PlanningPreference preference12 =
            new PlanningPreference(5, teacher.getSlots().get(2), location1, location2, student1);

        student1.setPreferences(new ArrayList<>(Stream.of(preference10,preference11,preference12).
            collect(Collectors.toList())));

        PlanningPreference preference20 =
            new PlanningPreference(6, teacher.getSlots().get(0), location2, location1, student2);
        PlanningPreference preference21 =
            new PlanningPreference(7, teacher.getSlots().get(1), location2, location1, student2);
        PlanningPreference preference22 =
            new PlanningPreference(8, teacher.getSlots().get(2), location2, location1, student2);
        student2.setPreferences(new ArrayList<>(Stream.of(preference20,preference21,preference22).
            collect(Collectors.toList())));

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();


        for (PlanningDrivingLesson lesson : solution.getLessons()){
            assertNotNull(lesson.getStudent(), "There are enough students for all slots");
        }

        assertEquals(2,(int)solution.getLessons().get(1).getStudent().getId(),
            "Student 2 must be put in the middle.");
    }
}
