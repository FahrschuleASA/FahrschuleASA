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

public class CategoryTest {

    PlanningTeacher teacher;
    PlanningLocation location = new PlanningLocation(49.2577, 7.0450);

    void init(List<DrivingCategory> categoryList, List<DrivingCategory> optionalDrivingCategories ,List<PlanningStudent> studentList){
        List<PlanningTimeSlot> slotList = Stream.of(new PlanningTimeSlot(0, 0, 90,
            0, categoryList, optionalDrivingCategories)).collect(Collectors.toList());

        teacher = new PlanningTeacher(0, true, slotList, new ArrayList<>(),studentList,
            true, false, dayTime(12, 0));
    }

    /**
     * There is a only one student (with driving category A).
     * The teacher selected A as driving category too.
     * The student must be assigned.
     */
    @Test
    void categoryTest0(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.A,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.A).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));

        student.setTeacher(teacher);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNotNull(solution.getLessons().get(0).getStudent(),"This task should be easily solvable");
        assertEquals(0,(int)solution.getLessons().get(0).getStudent().getId(),
            "The time slot must be assigned to student 0. Categories do match.");

    }

    /**
     * There is a only one student (with driving category A).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest1(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.A,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        student.setTeacher(teacher);

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category A).
     * The teacher selected B as driving category.
     * Furthermore the teacher selected A as optional driving category.
     * The student must be assigned to the slot.
     */
    @Test
    void categoryTest2(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.A,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            Stream.of(DrivingCategory.A).collect(Collectors.toList()),
            Stream.of(student).collect(Collectors.toList()));

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));

        student.setTeacher(teacher);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNotNull(solution.getLessons().get(0).getStudent(),"This task should be easily solvable");
        assertEquals(0,(int)solution.getLessons().get(0).getStudent().getId(),
            "The time slot must be assigned to student 0. Categories do match.");
    }

    /**
     * Student 0 and student 1 compete for one single slot.
     * Student 0 has driving category A and student 1 has driving category B.
     * The teacher selected B as driving category and A as optional driving category.
     * The slot must be assigned to student 1.
     */
    @Test
    void categoryTest3(){
        PlanningStudent student0 =new PlanningStudent((long) 0, true, DrivingCategory.A,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        PlanningStudent student1 =new PlanningStudent((long) 1, true, DrivingCategory.B,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());


        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            Stream.of(DrivingCategory.A).collect(Collectors.toList()),
            Stream.of(student0, student1).collect(Collectors.toList()));

        student0.setTeacher(teacher);
        student1.setTeacher(teacher);

        PlanningPreference preference0 =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student0);
        student0.setPreferences(new ArrayList<>(Stream.of(preference0).collect(Collectors.toList())));

        PlanningPreference preference1 =
            new PlanningPreference(1, teacher.getSlots().get(0), location, location, student1);
        student1.setPreferences(new ArrayList<>(Stream.of(preference1).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNotNull(solution.getLessons().get(0).getStudent(),"This task should be easily solvable");
        assertEquals(1,(int)solution.getLessons().get(0).getStudent().getId(),
            "The slot needs to be assigned to student 1 due to the teachers preferences regarding driving categories");
    }

    /**
     * There is a only one student (with driving category MOFA).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest4(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.MOFA,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category A1).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest5(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.A1,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);


        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category A2).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest6(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.A2,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);


        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category BE).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest7(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.BE,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category B96).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest8(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B96,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category L).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest9(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.L,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);


        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category AM).
     * The teacher selected B as driving category.
     * There is no optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest10(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.AM,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.B).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);


        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category B).
     * The teacher selected everything but B as driving category.
     * The teacher did not select any optional driving categories.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest11(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.MOFA, DrivingCategory.AM, DrivingCategory.A1, DrivingCategory.A2,
            DrivingCategory.A, DrivingCategory.B96, DrivingCategory.BE, DrivingCategory.L).collect(Collectors.toList()),
            new ArrayList<>(),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

    /**
     * There is a only one student (with driving category B).
     * The teacher selected everything but B as driving category.
     * The teacher selected everything but B as optional driving category.
     * No student must be assigned to the slot.
     */
    @Test
    void categoryTest12(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, null, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(DrivingCategory.MOFA, DrivingCategory.AM, DrivingCategory.A1, DrivingCategory.A2,
            DrivingCategory.A, DrivingCategory.B96, DrivingCategory.BE, DrivingCategory.L).collect(Collectors.toList()),
            Stream.of(DrivingCategory.MOFA, DrivingCategory.AM, DrivingCategory.A1, DrivingCategory.A2,
                DrivingCategory.A, DrivingCategory.B96, DrivingCategory.BE, DrivingCategory.L).collect(Collectors.toList()),
            Stream.of(student).collect(Collectors.toList()));

        student.setTeacher(teacher);

        PlanningPreference preference =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student);
        student.setPreferences(new ArrayList<>(Stream.of(preference).collect(Collectors.toList())));


        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        assertNull(solution.getLessons().get(0).getStudent(),
            "Driving categories do not match. No assignment is possible");
    }

}
