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

public class WantedSlotsTest {

    PlanningTeacher teacher;
    PlanningLocation location = new PlanningLocation(49.2577, 7.0450);
    List<PlanningTimeSlot> slotList = new ArrayList<>();


    void init(List<PlanningStudent> studentList, int numberOfSlots){

        List<DrivingCategory> categoryList = Stream.of(DrivingCategory.B).collect(Collectors.toList());
        List<DrivingCategory> optionalDrivingCategories = new ArrayList<>();

        for (int i=0; i< numberOfSlots; i++){
            slotList.add(new PlanningTimeSlot(i, i*90, (i+1)*90,
                0, categoryList, optionalDrivingCategories));
        }

        teacher = new PlanningTeacher(0, true, slotList, new ArrayList<>(),studentList,
            true, false, dayTime(24, 0));
    }

    void setPreferenceToAllSlots (PlanningStudent student){

        List<PlanningPreference> preferences = new ArrayList<>();

        for (PlanningTimeSlot slot: slotList){
            preferences.add(new PlanningPreference(slot.getId(), slot, location, location, student));
        }

        student.setPreferences(preferences);
    }

    /**
     * Student 0 and student 1 compete for three slots.
     * They are identical apart from the fact that student 0 wants one slot and student 1 wants two.
     * Therefore two of the three slots must be assigned to student 1.
     */
    @Test
    void wantedSlots0(){
        PlanningStudent student0 =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, teacher, 1, new ArrayList<>(), true, new ArrayList<>());

        PlanningStudent student1 =new PlanningStudent((long) 1, true, DrivingCategory.B,
            0, teacher, 2, new ArrayList<>(), true, new ArrayList<>());


        init(Stream.of(student0, student1).collect(Collectors.toList()),3);

        teacher.getStudents().forEach(this::setPreferenceToAllSlots);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int[] assignedSlots = new int[2];

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            assertNotNull(lesson.getStudent(), "There are enough students for all lessons");
            assignedSlots[(int)lesson.getStudent().getId()]++;
        }

        assertEquals(1, assignedSlots[0],
            "Student 0 must be assigned to only one slot");

        assertEquals(2, assignedSlots[1],
            "Student 1 must be assigned to two slots");
    }

    /**
     * A teacher provides three time slots.
     * There is only one student that wants two slots.
     * Make sure that maximally two slots are assigned.
     * */
    @Test
    void wantedSlots1(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, teacher, 2, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(student).collect(Collectors.toList()),3);

        teacher.getStudents().forEach(this::setPreferenceToAllSlots);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int assignedSlots = 0;

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            if (lesson.getStudent()!=null){
                assignedSlots++;
            }
        }

        if (assignedSlots>2){
            fail("The student must not be assigned to more than the two slots they want.");
        }
    }

    /**
     * A teacher provides three time slots.
     * There is only one student (who wants two slots).
     * Make sure that at least one slot is assigned.
     * */
    @Test
    void wantedSlots2(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, teacher, 2, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(student).collect(Collectors.toList()),3);

        teacher.getStudents().forEach(this::setPreferenceToAllSlots);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int assignedSlots = 0;

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            if (lesson.getStudent()!=null){
                assignedSlots++;
            }
        }

        if (assignedSlots<1){
            fail("The student must be assigned to at least one slot.");
        }
    }

    /**
     * A teacher provides three time slots.
     * There is only one student (who wants two slots).
     * Make sure that at least two slots are assigned.
     * */
    @Test
    void wantedSlots3(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, teacher, 2, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(student).collect(Collectors.toList()),3);

        teacher.getStudents().forEach(this::setPreferenceToAllSlots);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int assignedSlots = 0;

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            if (lesson.getStudent()!=null){
                assignedSlots++;
            }
        }

        if (assignedSlots<2){
            fail("The student must be assigned the two slots they want.");
        }
    }

    /**
     * A teacher provides three time slots.
     * There is only one student (who wants one slot).
     * Make sure that at least one slot is assigned.
     * */
    @Test
    void wantedSlots4(){
        PlanningStudent student =new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, teacher, 1, new ArrayList<>(), true, new ArrayList<>());

        init(Stream.of(student).collect(Collectors.toList()),3);

        teacher.getStudents().forEach(this::setPreferenceToAllSlots);

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();

        int assignedSlots = 0;

        for (PlanningDrivingLesson lesson : solution.getLessons()){
            if (lesson.getStudent()!=null){
                assignedSlots++;
            }
        }
        if (assignedSlots<1){
            fail("The student must be assigned to at least one slot.");
        }
    }

}
