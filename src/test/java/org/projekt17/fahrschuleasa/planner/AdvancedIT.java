package org.projekt17.fahrschuleasa.planner;

import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDate.ofYearDay;
import static org.junit.jupiter.api.Assertions.*;
import static org.projekt17.fahrschuleasa.planner.BaseTest.dayTime;

public class AdvancedIT {

    /**
     * There is only one student for a teacher which should be assigned.
     * There was no driving lesson in the previous week.
     */
    @Test
    void FirstWeek(){
        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, 0, 90, DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(
            slot0
        ).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(0, true, true, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,
            1, true, teacher);
        teacher.addStudent(student0);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);


        Planner planner = new Planner(teacher, ofYearDay(2020,6));
        Schedule solution = planner.makeQuickSchedule();

        assertNotNull(solution.getLessons().get(0));
    }

    /**
     * Student 1 holds a time slot and does not change their preferences.
     * Student 0 is added and wants the same slot.
     * The slot must still be assigned to student 1.
     */
    @Test
    void OldLesson(){
        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, 0, 90, DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(
            slot0
        ).collect(Collectors.toSet());

        // Teacher
        // slots changed = false
        teacher = helper.createTeacher(0, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,
            1, true, teacher);
        teacher.addStudent(student0);

        Student student1 = helper.createStudent(1,true, DrivingCategory.B,
            1, false, teacher);
        teacher.addStudent(student1);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);

        Preference preference1 = helper.createPreference(1,location,location,student1,slot0);
        student1.addPreference(preference1);

        // Assign old driving lesson to student 1
        LocalDate date = ofYearDay(2020,6);
        helper.addOldDrivingLesson(
            LocalDateTime.of(date, LocalTime.of(0,0)),
            LocalDateTime.of(date, LocalTime.of(1,30)),
            preference1, student1, slot0, teacher
        );

        Planner planner = new Planner(teacher, ofYearDay(2020,12));
        Schedule solution = planner.makeQuickSchedule();

        assertEquals((long) 1,(long) solution.getAllLessons().get(0).getStudentId());
    }

    /**
     * Block slot0 for the next week.
     * No student must be assigned to the slot.
     */
    @Test
    void BlockedLesson0(){
        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, 0, 90, DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(
            slot0
        ).collect(Collectors.toSet());

        // Teacher
        // slots changed = false
        teacher = helper.createTeacher(0, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,
            1, true, teacher);
        teacher.addStudent(student0);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);

        // Block the slot
        slot0.setBlockedDates(Stream.of(ofYearDay(2020,13)).collect(Collectors.toSet()));

        Planner planner = new Planner(teacher, ofYearDay(2020,12));
        Schedule solution = planner.makeQuickSchedule();

        assertTrue(solution.getAllLessons().isEmpty());
    }

    /**
     * Student 0 holds a time slot and does not change their preferences.
     * Block the slot for the next week.
     * No student must be assigned to the slot.
     */
    @Test
    void BlockedLesson1(){
        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, 0, 90, DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(
            slot0
        ).collect(Collectors.toSet());

        // Teacher
        // slots changed = false
        teacher = helper.createTeacher(0, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,
            1, true, teacher);
        teacher.addStudent(student0);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);


        // Assign old driving lesson to student 0
        LocalDate date = ofYearDay(2020,6);
        helper.addOldDrivingLesson(
            LocalDateTime.of(date, LocalTime.of(0,0)),
            LocalDateTime.of(date, LocalTime.of(1,30)),
            preference0, student0, slot0, teacher
        );

        // Block the slot
        slot0.setBlockedDates(Stream.of(ofYearDay(2020,13)).collect(Collectors.toSet()));

        Planner planner = new Planner(teacher, ofYearDay(2020,12));
        Schedule solution = planner.makeQuickSchedule();

        assertTrue(solution.getAllLessons().isEmpty());
    }

    /**
     * Construct some schedule for monday.
     * After the week all students decided to keep their slots.
     * Therefore both schedules must coincide.
     */
    @Test
    void ConsecutiveWeekTest0() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(8, 0), dayTime(9, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(42, dayTime(9, 30), dayTime(11, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(111, dayTime(11, 0), dayTime(12, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot3 = helper.createTimeSlot(4224, dayTime(12, 30), dayTime(14, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot4 = helper.createTimeSlot(1234567, dayTime(14, 0), dayTime(15, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot5 = helper.createTimeSlot(9234, dayTime(15, 30), dayTime(17, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot6 = helper.createTimeSlot(6926, dayTime(17, 0), dayTime(18, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot7 = helper.createTimeSlot(245, dayTime(18, 30), dayTime(20, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2, slot3, slot4, slot5, slot6, slot7).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(8359,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1873,true, DrivingCategory.B,2, true, teacher);
        Student student2 = helper.createStudent(2842,true, DrivingCategory.B,1, true, teacher);
        Student student3 = helper.createStudent(3238,true, DrivingCategory.B,2, true, teacher);
        Student student4 = helper.createStudent(4964,true, DrivingCategory.B,2, true, teacher);
        Student student5 = helper.createStudent(5623,true, DrivingCategory.B,2, true, teacher);
        Student student6 = helper.createStudent(6238,true, DrivingCategory.B,1, true, teacher);
        Student student7 = helper.createStudent(7256,true, DrivingCategory.B,2, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);
        teacher.addStudent(student3);
        teacher.addStudent(student4);
        teacher.addStudent(student5);
        teacher.addStudent(student6);
        teacher.addStudent(student7);

        // Preferences
        Preference preference00 = helper.createPreference(0,location,location,student0,slot0);
        Preference preference01 = helper.createPreference(1,location,location,student0,slot7);
        Preference preference02 = helper.createPreference(2,location,location,student0,slot3);
        student0.addPreference(preference00);
        student0.addPreference(preference01);
        student0.addPreference(preference02);
        Preference preference10 = helper.createPreference(3,location,location,student1,slot0);
        student1.addPreference(preference10);
        Preference preference20 = helper.createPreference(4,location,location,student2,slot6);
        Preference preference21 = helper.createPreference(5,location,location,student2,slot5);
        student2.addPreference(preference20);
        student2.addPreference(preference21);
        Preference preference30 = helper.createPreference(6,location,location,student3,slot1);
        student3.addPreference(preference30);
        Preference preference40 = helper.createPreference(7,location,location,student4,slot2);
        student4.addPreference(preference40);
        Preference preference50 = helper.createPreference(8,location,location,student5,slot3);
        student5.addPreference(preference50);
        Preference preference60 = helper.createPreference(9,location,location,student6,slot4);
        student6.addPreference(preference60);
        Preference preference70 = helper.createPreference(10,location,location,student7,slot5);
        student7.addPreference(preference70);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        teacher.getStudents().forEach(student -> student.setChangedPreferences(false));

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
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
     * Construct some schedule for monday.
     * After the week all students decided to keep their slots.
     * Additionally all students who were unsuccessful "change their preferences".
     * The schedule must remain the same anyway and still meet the legal requirements.
     */
    @Test
    void ConsecutiveWeekTest1() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(8, 0), dayTime(9, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(9, 30), dayTime(11, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(11, 0), dayTime(12, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot3 = helper.createTimeSlot(3, dayTime(12, 30), dayTime(14, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot4 = helper.createTimeSlot(4, dayTime(14, 0), dayTime(15, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot5 = helper.createTimeSlot(5, dayTime(15, 30), dayTime(17, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot6 = helper.createTimeSlot(6, dayTime(17, 0), dayTime(18, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot7 = helper.createTimeSlot(7, dayTime(18, 30), dayTime(20, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());

        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2, slot3, slot4, slot5, slot6, slot7).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,true, DrivingCategory.B,1, true, teacher);
        Student student3 = helper.createStudent(3,true, DrivingCategory.B,1, true, teacher);
        Student student4 = helper.createStudent(4,true, DrivingCategory.B,1, true, teacher);
        Student student5 = helper.createStudent(5,true, DrivingCategory.B,1, true, teacher);
        Student student6 = helper.createStudent(6,true, DrivingCategory.B,1, true, teacher);
        Student student7 = helper.createStudent(7,true, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);
        teacher.addStudent(student3);
        teacher.addStudent(student4);
        teacher.addStudent(student5);
        teacher.addStudent(student6);
        teacher.addStudent(student7);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);
        Preference preference3 = helper.createPreference(3,location,location,student3,slot3);
        student3.addPreference(preference3);
        Preference preference4 = helper.createPreference(4,location,location,student4,slot4);
        student4.addPreference(preference4);
        Preference preference5 = helper.createPreference(5,location,location,student5,slot5);
        student5.addPreference(preference5);
        Preference preference6 = helper.createPreference(6,location,location,student6,slot6);
        student6.addPreference(preference6);
        Preference preference7 = helper.createPreference(7,location,location,student7,slot7);
        student7.addPreference(preference7);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (Student student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent().getId()==student.getId()) assigned=true;
            }
            student.setChangedPreferences(!assigned);
        }

        int counter=0;
        for (PlanningDrivingLesson lesson : firstSchedule.getLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the first week. You assigned "+counter+".");

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
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

        counter=0;
        for (PlanningDrivingLesson lesson : secondSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the second week. You assigned "+counter+".");
    }


    /**
     * Construct some schedule for monday.
     * After the week 3 students who got a lesson are now inactive.
     * The remaining 5 students must be assigned to a lesson anyway.
     */
    @Test
    void ConsecutiveWeekTest2() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(8, 0), dayTime(9, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(9, 30), dayTime(11, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(11, 0), dayTime(12, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot3 = helper.createTimeSlot(3, dayTime(12, 30), dayTime(14, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot4 = helper.createTimeSlot(4, dayTime(14, 0), dayTime(15, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot5 = helper.createTimeSlot(5, dayTime(15, 30), dayTime(17, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot6 = helper.createTimeSlot(6, dayTime(17, 0), dayTime(18, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot7 = helper.createTimeSlot(7, dayTime(18, 30), dayTime(20, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());

        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2, slot3, slot4, slot5, slot6, slot7).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,true, DrivingCategory.B,1, true, teacher);
        Student student3 = helper.createStudent(3,true, DrivingCategory.B,1, true, teacher);
        Student student4 = helper.createStudent(4,true, DrivingCategory.B,1, true, teacher);
        Student student5 = helper.createStudent(5,true, DrivingCategory.B,1, true, teacher);
        Student student6 = helper.createStudent(6,true, DrivingCategory.B,1, true, teacher);
        Student student7 = helper.createStudent(7,true, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);
        teacher.addStudent(student3);
        teacher.addStudent(student4);
        teacher.addStudent(student5);
        teacher.addStudent(student6);
        teacher.addStudent(student7);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);
        Preference preference3 = helper.createPreference(3,location,location,student3,slot3);
        student3.addPreference(preference3);
        Preference preference4 = helper.createPreference(4,location,location,student4,slot4);
        student4.addPreference(preference4);
        Preference preference5 = helper.createPreference(5,location,location,student5,slot5);
        student5.addPreference(preference5);
        Preference preference6 = helper.createPreference(6,location,location,student6,slot6);
        student6.addPreference(preference6);
        Preference preference7 = helper.createPreference(7,location,location,student7,slot7);
        student7.addPreference(preference7);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (Student student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent().getId()==student.getId()) assigned=true;
            }
            student.setChangedPreferences(!assigned);
        }

        int counter=0;
        for (PlanningDrivingLesson lesson : firstSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the first week. You assigned "+counter+".");

        student0.setActive(false);
        student1.setActive(false);
        student2.setActive(false);

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        counter=0;
        for (PlanningDrivingLesson lesson : secondSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the second week. You assigned "+counter+".");
    }

    /**
     * For the first week create an easy schedule for five lessons on monday.
     * So the first 5 slots must be assigned to a student.
     * After that, create a manual lesson after those 5 existing ones.
     * This obviously requires to drop one of the old lessons in the next iteration of the planner
     * to keep te legal requirements.
     */
    @Test
    void ManualLesson0() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(8, 0), dayTime(9, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(9, 30), dayTime(11, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(11, 0), dayTime(12, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot3 = helper.createTimeSlot(3, dayTime(12, 30), dayTime(14, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot4 = helper.createTimeSlot(4, dayTime(14, 0), dayTime(15, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot5 = helper.createTimeSlot(5, dayTime(15, 30), dayTime(17, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot6 = helper.createTimeSlot(6, dayTime(17, 0), dayTime(18, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());

        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2, slot3, slot4, slot5, slot6).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,true, DrivingCategory.B,1, true, teacher);
        Student student3 = helper.createStudent(3,true, DrivingCategory.B,1, true, teacher);
        Student student4 = helper.createStudent(4,true, DrivingCategory.B,1, true, teacher);
        Student student5 = helper.createStudent(5,true, DrivingCategory.B,1, true, teacher);
        Student student6 = helper.createStudent(6,false, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);
        teacher.addStudent(student3);
        teacher.addStudent(student4);
        teacher.addStudent(student5);
        teacher.addStudent(student6);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);
        Preference preference3 = helper.createPreference(3,location,location,student3,slot3);
        student3.addPreference(preference3);
        Preference preference4 = helper.createPreference(4,location,location,student4,slot4);
        student4.addPreference(preference4);
        Preference preference5 = helper.createPreference(5,location,location,student5,slot5);
        student5.addPreference(preference5);
        Preference preference6 = helper.createPreference(6,location,location,student6,slot6);
        student6.addPreference(preference6);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        int counter=0;
        for (PlanningDrivingLesson lesson : firstSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the first week. You assigned "+counter+".");

        // set a manual driving lesson for student 6 and slot 6
        teacher.getStudents().forEach(student -> student.setChangedPreferences(false));
        student6.setActive(true);
        helper.addManualDrivingLesson(
            LocalDateTime.of(2020,1,13,17,0),
            LocalDateTime.of(2020,1,13,18,30),
            preference6,
            student6,
            slot6,
            teacher
        );
        slot6.setBlockedDates(Stream.of(ofYearDay(2020,13)).collect(Collectors.toSet()));

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        counter=0;
        for (PlanningDrivingLesson lesson : secondSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the second week. You assigned "+counter+".");
    }

    /**
     * For the first week create an easy schedule with one lesson on monday evening and one on tuesday morning.
     * Those two lessons are exactly 11 hours apart.
     * After that, create a manual lesson in between.
     * This obviously requires to drop one of the old lessons in the next iteration of the planner
     * to keep te legal requirements.
     */
    @Test
    void ManualLesson1() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(19, 30), dayTime(21, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(8, 0), dayTime(9, 30), DayOfWeek.TU, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(21, 0), dayTime(22, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,false, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (Student student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent().getId()==student.getId()) assigned=true;
            }
            student.setChangedPreferences(!assigned);
        }

        int counter=0;
        for (PlanningDrivingLesson lesson : firstSchedule.getLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=2) fail("You must assign 2 lessons in the first week. You assigned "+counter+".");

        // set a manual driving lesson for student 6 and slot 6
        teacher.getStudents().forEach(student -> student.setChangedPreferences(false));
        student2.setActive(true);
        helper.addManualDrivingLesson(
            LocalDateTime.of(2020,1,13,21,0),
            LocalDateTime.of(2020,1,13,22,30),
            preference2,
            student2,
            slot2,
            teacher
        );
        slot2.setBlockedDates(Stream.of(ofYearDay(2020,13)).collect(Collectors.toSet()));

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);


        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        counter=0;
        boolean student0_assigned =false;
        boolean student1_assigned =false;
        for (PlanningDrivingLesson lesson : secondSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++; else continue;
            if (lesson.getStudent().getId()==0) student0_assigned=true;
            if (lesson.getStudent().getId()==1) student1_assigned=true;
        }
        if (student0_assigned&&student1_assigned) fail("You must drop the driving lesson for either student 0 or student 1 in the second week");
        if (counter!=2) fail("You must assign 2 lessons in the second week. You assigned "+counter+".");
    }

    /**
     * Construct some schedule for monday.
     * There are 6 slots which are all consecutive.
     * The planner must assign 5 of them and leaving no gap in between.
     */
    @Test
    void GapTest() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(8, 0), dayTime(9, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(9, 30), dayTime(11, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(11, 0), dayTime(12, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot3 = helper.createTimeSlot(3, dayTime(12, 30), dayTime(14, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot4 = helper.createTimeSlot(4, dayTime(14, 0), dayTime(15, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot5 = helper.createTimeSlot(5, dayTime(15, 30), dayTime(17, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());

        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2, slot3, slot4, slot5).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,true, DrivingCategory.B,1, true, teacher);
        Student student3 = helper.createStudent(3,true, DrivingCategory.B,1, true, teacher);
        Student student4 = helper.createStudent(4,true, DrivingCategory.B,1, true, teacher);
        Student student5 = helper.createStudent(5,true, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);
        teacher.addStudent(student3);
        teacher.addStudent(student4);
        teacher.addStudent(student5);


        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);
        Preference preference3 = helper.createPreference(3,location,location,student3,slot3);
        student3.addPreference(preference3);
        Preference preference4 = helper.createPreference(4,location,location,student4,slot4);
        student4.addPreference(preference4);
        Preference preference5 = helper.createPreference(5,location,location,student5,slot5);
        student5.addPreference(preference5);

        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule schedule = firstPlanner.makeQuickSchedule();


        boolean first_assigned =false;
        boolean last_assigned = false;
        int counter=0;
        for (PlanningDrivingLesson lesson : schedule.getLessons()){
            if (lesson.getStudent()!=null) counter++; else continue;
            if (lesson.getStudent().getId()==0) first_assigned=true;
            if (lesson.getStudent().getId()==5) last_assigned=true;
        }
        if (counter!=5) fail("You must assign 5 lessons. You assigned "+counter+".");
        if (first_assigned&&last_assigned) fail("You must leave no gap in between");

    }

    /**
     * For the first week create an easy schedule for five lessons on monday.
     * So the first 5 slots must be assigned to a student.
     * After that, create a manual lesson in the middle of the night but belonging to the same work day.
     * This obviously requires to drop one of the old lessons in the next iteration of the planner
     * to keep te legal requirements.
     */
    @Test
    void ManualLesson2() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(8, 0), dayTime(9, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(9, 30), dayTime(11, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(11, 0), dayTime(12, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot3 = helper.createTimeSlot(3, dayTime(12, 30), dayTime(14, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot4 = helper.createTimeSlot(4, dayTime(14, 0), dayTime(15, 30), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot5 = helper.createTimeSlot(5, dayTime(15, 30), dayTime(17, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot6 = helper.createTimeSlot(6, dayTime(23, 30), dayTime(1, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());

        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2, slot3, slot4, slot5, slot6).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,true, DrivingCategory.B,1, true, teacher);
        Student student3 = helper.createStudent(3,true, DrivingCategory.B,1, true, teacher);
        Student student4 = helper.createStudent(4,true, DrivingCategory.B,1, true, teacher);
        Student student5 = helper.createStudent(5,true, DrivingCategory.B,1, true, teacher);
        Student student6 = helper.createStudent(6,false, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);
        teacher.addStudent(student3);
        teacher.addStudent(student4);
        teacher.addStudent(student5);
        teacher.addStudent(student6);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);
        Preference preference3 = helper.createPreference(3,location,location,student3,slot3);
        student3.addPreference(preference3);
        Preference preference4 = helper.createPreference(4,location,location,student4,slot4);
        student4.addPreference(preference4);
        Preference preference5 = helper.createPreference(5,location,location,student5,slot5);
        student5.addPreference(preference5);
        Preference preference6 = helper.createPreference(6,location,location,student6,slot6);
        student6.addPreference(preference6);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        int counter=0;
        for (PlanningDrivingLesson lesson : firstSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the first week. You assigned "+counter+".");

        // set a manual driving lesson for student 6 and slot 6
        student6.setActive(true);
        teacher.getStudents().forEach(student -> student.setChangedPreferences(false));
        helper.addManualDrivingLesson(
            LocalDateTime.of(2020,1,13,23,30),
            LocalDateTime.of(2020,1,14,1,0),
            preference6,
            student6,
            slot6,
            teacher
        );
        slot6.setBlockedDates(Stream.of(ofYearDay(2020,13)).collect(Collectors.toSet()));

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        counter=0;
        for (PlanningDrivingLesson lesson : secondSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=5) fail("You must assign 5 lessons in the second week. You assigned "+counter+".");
    }

    /**
     * For the first week create an easy schedule with one lesson on monday evening and one on tuesday morning.
     * Those two lessons are exactly 11 hours apart.
     * After that, create a manual lesson in between in the middle of the night.
     * This obviously requires to drop one of the old lessons in the next iteration of the planner
     * to keep te legal requirements.
     */
    @Test
    void ManualLesson3() {

        ConstructionHelper helper = new ConstructionHelper();
        Teacher teacher;

        // Locations
        Location location = helper.createLocation(49.2577, 7.0450);

        // Driving Categories
        Set<DrivingCategory> preferredCategories = Stream.of(DrivingCategory.B).collect(Collectors.toSet());

        // TimeSlots
        TimeSlot slot0 = helper.createTimeSlot(0, dayTime(19, 30), dayTime(21, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot1 = helper.createTimeSlot(1, dayTime(8, 0), dayTime(9, 30), DayOfWeek.TU, preferredCategories, new HashSet<>(), new HashSet<>());
        TimeSlot slot2 = helper.createTimeSlot(2, dayTime(23, 30), dayTime(1, 0), DayOfWeek.MO, preferredCategories, new HashSet<>(), new HashSet<>());
        Set<TimeSlot> slots = Stream.of(slot0,slot1, slot2).collect(Collectors.toSet());

        // Teacher
        teacher = helper.createTeacher(42, true, false, false, slots,new HashSet<>());

        // Students
        Student student0 = helper.createStudent(0,true, DrivingCategory.B,1, true, teacher);
        Student student1 = helper.createStudent(1,true, DrivingCategory.B,1, true, teacher);
        Student student2 = helper.createStudent(2,false, DrivingCategory.B,1, true, teacher);
        teacher.addStudent(student0);
        teacher.addStudent(student1);
        teacher.addStudent(student2);

        // Preferences
        Preference preference0 = helper.createPreference(0,location,location,student0,slot0);
        student0.addPreference(preference0);
        Preference preference1 = helper.createPreference(1,location,location,student1,slot1);
        student1.addPreference(preference1);
        Preference preference2 = helper.createPreference(2,location,location,student2,slot2);
        student2.addPreference(preference2);

        // call the planner for the first week
        Planner firstPlanner = new Planner(teacher, ofYearDay(2020,6));
        Schedule firstSchedule = firstPlanner.makeQuickSchedule();

        for (Student student : teacher.getStudents()){
            boolean assigned=false;
            for (PlanningDrivingLesson drivingLesson : firstSchedule.getLessons()){
                if (drivingLesson.getStudent()==null)continue;
                if (drivingLesson.getStudent().getId()==student.getId()) assigned=true;
            }
            student.setChangedPreferences(!assigned);
        }

        int counter=0;
        for (PlanningDrivingLesson lesson : firstSchedule.getLessons()){
            if (lesson.getStudent()!=null) counter++;
        }
        if (counter!=2) fail("You must assign 2 lessons in the first week. You assigned "+counter+".");

        // set a manual driving lesson for student 6 and slot 6
        teacher.getStudents().forEach(student -> student.setChangedPreferences(false));
        student2.setActive(true);
        helper.addManualDrivingLesson(
            LocalDateTime.of(2020,1,13,23,30),
            LocalDateTime.of(2020,1,14,1,0),
            preference2,
            student2,
            slot2,
            teacher
        );
        slot2.setBlockedDates(Stream.of(ofYearDay(2020,13)).collect(Collectors.toSet()));

        helper.addScheduledDrivingLessons(teacher,ofYearDay(2020,6), firstSchedule);

        // call the planner for the second week
        Planner secondPlanner = new Planner(teacher, ofYearDay(2020,13));
        Schedule secondSchedule = secondPlanner.makeQuickSchedule();

        counter=0;
        boolean student0_assigned =false;
        boolean student1_assigned =false;
        for (PlanningDrivingLesson lesson : secondSchedule.getAllLessons()){
            if (lesson.getStudent()!=null) counter++; else continue;
            if (lesson.getStudent().getId()==0) student0_assigned=true;
            if (lesson.getStudent().getId()==1) student1_assigned=true;
        }
        if (student0_assigned&&student1_assigned) fail("You must drop the driving lesson for either student 0 or student 1 in the second week");
        if (counter!=2) fail("You must assign 2 lessons in the second week. You assigned "+counter+".");
    }
}

