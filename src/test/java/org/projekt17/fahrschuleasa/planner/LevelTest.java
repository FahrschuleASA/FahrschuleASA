package org.projekt17.fahrschuleasa.planner;

import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.projekt17.fahrschuleasa.planner.BaseTest.dayTime;

public class LevelTest {

    /**
     * To students which are absolutely equal apart from id and level compete for one single time slot.
     * Student 1 must be favored due to their superior level.
     */
    @Test
    void levelTest(){
        PlanningTeacher teacher;

        PlanningLocation location = new PlanningLocation(49.2577, 7.0450);

        List<DrivingCategory> categoryList = Stream.of(DrivingCategory.B).collect(Collectors.toList());
        List<PlanningTimeSlot> slotList = Stream.of(new PlanningTimeSlot(0, 0, 90,
            0, categoryList, new ArrayList<>())).collect(Collectors.toList());
        List<PlanningStudent> studentList = new ArrayList<>();


        teacher = new PlanningTeacher(0, true, slotList, new ArrayList<>(),studentList,
            true, false, dayTime(12, 0));

        PlanningStudent student0 = new PlanningStudent((long) 0, true, DrivingCategory.B,
            0, teacher, 1, new ArrayList<>(), true, new ArrayList<>());
        studentList.add(student0);

        PlanningStudent student1 = new PlanningStudent((long) 1, true, DrivingCategory.B,
            42, teacher, 1, new ArrayList<>(), true, new ArrayList<>());
        studentList.add(student1);

        teacher.setSlots(slotList);
        teacher.setStudents(studentList);

        PlanningPreference preference0 =
            new PlanningPreference(0, teacher.getSlots().get(0), location, location, student0);
        student0.setPreferences(new ArrayList<>(Stream.of(preference0).collect(Collectors.toList())));

        PlanningPreference preference1 =
            new PlanningPreference(1, teacher.getSlots().get(0), location, location, student1);
        student1.setPreferences(new ArrayList<>(Stream.of(preference1).collect(Collectors.toList())));

        Planner planner = new Planner(teacher);
        Schedule solution = planner.makeQuickSchedule();


        assertEquals(2,solution.getLessons().get(0).getPossibleStudents().size(),
            "The time slot has 2 possible students.");

        assertNotNull(solution.getLessons().get(0).getStudent(),"This task should be easily solvable");
        assertEquals(1,(int)solution.getLessons().get(0).getStudent().getId(),
            "The time slot must be assigned to student 1");

    }
}
