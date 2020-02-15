package org.projekt17.fahrschuleasa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projekt17.fahrschuleasa.EntityCreationHelper;
import org.projekt17.fahrschuleasa.FahrschuleAsaApp;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.planner.PlanningDrivingLesson;
import org.projekt17.fahrschuleasa.planner.Schedule;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningStudent;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTeacher;
import org.projekt17.fahrschuleasa.planner.problemFacts.PlanningTimeSlot;
import org.projekt17.fahrschuleasa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FahrschuleAsaApp.class)
public class DrivingLessonServiceIT {

    @Autowired
    private DrivingLessonService drivingLessonService;

    @Autowired
    private DrivingLessonRepository drivingLessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    private EntityCreationHelper entityCreationHelper;

    @BeforeEach
    public void setup() {
        entityCreationHelper = new EntityCreationHelper();
    }

    @Transactional
    @Test
    public void simpleCreateDrivingLessonsTest() {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student1 = entityCreationHelper.createEntityStudent(false);
        student1.getUser().setLogin("student1");
        Student student2 = entityCreationHelper.createEntityStudent(true);
        student2.getUser().setLogin("student2");
        studentRepository.save(student1);
        studentRepository.save(student2);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setTeacher(teacher);
        timeSlot.setDay(DayOfWeek.WE);
        timeSlot.setBegin(555);
        timeSlot.setEnd(645);
        timeSlotRepository.save(timeSlot);
        Preference preference = entityCreationHelper.createEntityPreference(false, false);
        preference.setStudent(student1);
        preference.setTimeSlot(timeSlot);
        preferenceRepository.save(preference);

        PlanningTeacher planningTeacher = new PlanningTeacher();
        planningTeacher.setId(teacher.getId());
        PlanningStudent s1 = new PlanningStudent();
        s1.setId(student1.getId());
        s1.setTeacher(planningTeacher);
        PlanningStudent s2 = new PlanningStudent();
        s2.setId(student2.getId());
        s2.setTeacher(planningTeacher);

        PlanningDrivingLesson lesson = new PlanningDrivingLesson();
        PlanningTimeSlot slot = new PlanningTimeSlot();
        slot.setId(timeSlot.getId());
        lesson.setStudent(s1);
        List<PlanningStudent> possibleStudents = new ArrayList<>();
        possibleStudents.add(s2);
        lesson.setPossibleStudents(possibleStudents);
        lesson.setSlot(slot);
        List<PlanningDrivingLesson> lessons = new ArrayList<>();
        lessons.add(lesson);

        Schedule solution = new Schedule(planningTeacher, null, lessons, new ArrayList<>(), 7, null);

        drivingLessonService.createDrivingLessons(solution, LocalDate.of(2020,12,14));

        List<DrivingLesson> drivingLessons = drivingLessonRepository.findAllByBeginEquals(LocalDateTime.of(2020,12,16,9,15));

        assertThat(drivingLessons).hasSize(1);
        DrivingLesson drivingLesson = drivingLessons.get(0);
        assertThat(drivingLesson.getManualLesson()).isFalse();
        assertThat(drivingLesson.getDriver().getId()).isEqualTo(student1.getId());
        assertThat(drivingLesson.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(drivingLesson.getBegin()).isEqualTo(LocalDateTime.of(2020, 12, 16, 9, 15));
        assertThat(drivingLesson.getEnd()).isEqualTo(LocalDateTime.of(2020, 12, 16, 10, 45));
        assertThat(drivingLesson.getOptionalStudents()).hasSize(1);
        assertThat(drivingLesson.getOptionalStudents().toArray(new Student[0])[0].getId()).isEqualTo(student2.getId());
        assertThat(drivingLesson.getDriver().getLastScheduledTimeSlots()).hasSize(1);
        assertThat(drivingLesson.getDriver().getLastScheduledTimeSlots().toArray(new TimeSlot[0])[0].getId()).isEqualTo(timeSlot.getId());
        student1 = studentRepository.findById(student1.getId()).orElse(null);
        assertThat(student1.getDrivingLessons()).hasSize(1);
        assertThat(student1.getDrivingLessons()).contains(drivingLesson);
        student2 = studentRepository.findById(student2.getId()).orElse(null);
        assertThat(student2.getDrivingLessons()).hasSize(0);
        assertThat(student2.getLastScheduledTimeSlots()).hasSize(0);
        teacher = teacherRepository.findById(teacher.getId()).orElse(null);
        assertThat(teacher.getDrivingLessons()).hasSize(1);
        assertThat(teacher.getDrivingLessons()).contains(drivingLesson);
    }

    @Transactional
    @Test
    public void createDrivingLessonsTestManualLesson() {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student1 = entityCreationHelper.createEntityStudent(false);
        student1.getUser().setLogin("student1");
        Student student2 = entityCreationHelper.createEntityStudent(true);
        student2.getUser().setLogin("student2");
        studentRepository.save(student1);
        studentRepository.save(student2);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setTeacher(teacher);
        timeSlot.setDay(DayOfWeek.WE);
        timeSlot.setBegin(555);
        timeSlot.setEnd(645);
        timeSlotRepository.save(timeSlot);
        Preference preference = entityCreationHelper.createEntityPreference(false, false);
        preference.setStudent(student1);
        preference.setTimeSlot(timeSlot);
        preferenceRepository.save(preference);

        PlanningTeacher planningTeacher = new PlanningTeacher();
        planningTeacher.setId(teacher.getId());
        PlanningStudent s1 = new PlanningStudent();
        s1.setId(student1.getId());
        s1.setTeacher(planningTeacher);
        PlanningStudent s2 = new PlanningStudent();
        s2.setId(student2.getId());
        s2.setTeacher(planningTeacher);

        PlanningDrivingLesson lesson = new PlanningDrivingLesson();
        lesson.setManualLesson(true);
        PlanningTimeSlot slot = new PlanningTimeSlot();
        slot.setId(timeSlot.getId());
        lesson.setStudent(s1);
        List<PlanningStudent> possibleStudents = new ArrayList<>();
        possibleStudents.add(s2);
        lesson.setPossibleStudents(possibleStudents);
        lesson.setSlot(slot);
        List<PlanningDrivingLesson> lessons = new ArrayList<>();
        lessons.add(lesson);

        Schedule solution = new Schedule(planningTeacher, null, lessons, new ArrayList<>(), 7, null);

        drivingLessonService.createDrivingLessons(solution, LocalDate.of(2020,12,14));

        List<DrivingLesson> drivingLessons = drivingLessonRepository.findAllByBeginEquals(LocalDateTime.of(2020,12,16,9,15));

        //the lesson is a manual lesson and therefor should not be saved in the database
        assertThat(drivingLessons).hasSize(0);
        student1 = studentRepository.findById(student1.getId()).orElse(null);
        assertThat(student1.getDrivingLessons()).hasSize(0);
        assertThat(student1.getLastScheduledTimeSlots()).hasSize(0);
    }

    @Transactional
    @Test
    public void createDrivingLessonsTestStudentInOptionals() {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student1 = entityCreationHelper.createEntityStudent(false);
        student1.getUser().setLogin("student1");
        Student student2 = entityCreationHelper.createEntityStudent(true);
        student2.getUser().setLogin("student2");
        studentRepository.save(student1);
        studentRepository.save(student2);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setTeacher(teacher);
        timeSlot.setDay(DayOfWeek.WE);
        timeSlot.setBegin(555);
        timeSlot.setEnd(645);
        timeSlotRepository.save(timeSlot);
        Preference preference = entityCreationHelper.createEntityPreference(false, false);
        preference.setStudent(student1);
        preference.setTimeSlot(timeSlot);
        preferenceRepository.save(preference);

        PlanningTeacher planningTeacher = new PlanningTeacher();
        planningTeacher.setId(teacher.getId());
        PlanningStudent s1 = new PlanningStudent();
        s1.setId(student1.getId());
        s1.setTeacher(planningTeacher);
        PlanningStudent s2 = new PlanningStudent();
        s2.setId(student2.getId());
        s2.setTeacher(planningTeacher);

        PlanningDrivingLesson lesson = new PlanningDrivingLesson();
        PlanningTimeSlot slot = new PlanningTimeSlot();
        slot.setId(timeSlot.getId());
        lesson.setStudent(s1);
        List<PlanningStudent> possibleStudents = new ArrayList<>();
        possibleStudents.add(s2);
        //add also the student s1 as possible student
        possibleStudents.add(s1);
        lesson.setPossibleStudents(possibleStudents);
        lesson.setSlot(slot);
        List<PlanningDrivingLesson> lessons = new ArrayList<>();
        lessons.add(lesson);

        Schedule solution = new Schedule(planningTeacher, null, lessons, new ArrayList<>(), 7, null);

        drivingLessonService.createDrivingLessons(solution, LocalDate.of(2020,12,14));

        List<DrivingLesson> drivingLessons = drivingLessonRepository.findAllByBeginEquals(LocalDateTime.of(2020,12,16,9,15));

        assertThat(drivingLessons).hasSize(1);
        DrivingLesson drivingLesson = drivingLessons.get(0);
        assertThat(drivingLesson.getOptionalStudents()).hasSize(1);
        assertThat(drivingLesson.getOptionalStudents().toArray(new Student[0])[0].getId()).isEqualTo(student2.getId());
    }

    @Transactional
    @Test
    public void createTwoDrivingLessonsTest() {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student1 = entityCreationHelper.createEntityStudent(false);
        student1.getUser().setLogin("student1");
        Student student2 = entityCreationHelper.createEntityStudent(true);
        student2.getUser().setLogin("student2");
        studentRepository.save(student1);
        studentRepository.save(student2);
        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setTeacher(teacher);
        timeSlot1.setDay(DayOfWeek.WE);
        timeSlot1.setBegin(555);
        timeSlot1.setEnd(645);
        timeSlotRepository.save(timeSlot1);
        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setTeacher(teacher);
        timeSlot2.setDay(DayOfWeek.WE);
        timeSlot2.setBegin(780);
        timeSlot2.setEnd(870);
        timeSlotRepository.save(timeSlot2);
        Preference preference1 = entityCreationHelper.createEntityPreference(false, false);
        preference1.setStudent(student1);
        preference1.setTimeSlot(timeSlot1);
        preferenceRepository.save(preference1);
        Preference preference2 = entityCreationHelper.createEntityPreference(false, false);
        preference2.setStudent(student2);
        preference2.setTimeSlot(timeSlot2);
        preferenceRepository.save(preference2);

        PlanningTeacher planningTeacher = new PlanningTeacher();
        planningTeacher.setId(teacher.getId());

        PlanningStudent s1 = new PlanningStudent();
        s1.setId(student1.getId());
        s1.setTeacher(planningTeacher);
        PlanningStudent s2 = new PlanningStudent();
        s2.setId(student2.getId());
        s2.setTeacher(planningTeacher);

        //lesson 1
        PlanningDrivingLesson lesson1 = new PlanningDrivingLesson();
        PlanningTimeSlot slot1 = new PlanningTimeSlot();
        slot1.setId(timeSlot1.getId());
        lesson1.setStudent(s1);
        List<PlanningStudent> possibleStudents = new ArrayList<>();
        possibleStudents.add(s2);
        lesson1.setPossibleStudents(possibleStudents);
        lesson1.setSlot(slot1);

        //lesson 2
        PlanningDrivingLesson lesson2 = new PlanningDrivingLesson();
        PlanningTimeSlot slot2 = new PlanningTimeSlot();
        slot2.setId(timeSlot2.getId());
        lesson2.setStudent(s2);
        lesson2.setPossibleStudents(new ArrayList<>());
        lesson2.setSlot(slot2);

        List<PlanningDrivingLesson> lessons = new ArrayList<>();
        lessons.add(lesson1);
        lessons.add(lesson2);

        Schedule solution = new Schedule(planningTeacher, null, lessons, new ArrayList<>(), 7, null);

        drivingLessonService.createDrivingLessons(solution, LocalDate.of(2020,12,14));

        List<DrivingLesson> drivingLessons = drivingLessonRepository.findAllByBeginGreaterThanEqual(LocalDateTime.of(2020,12,16,9,15));

        assertThat(drivingLessons).hasSize(2);
        student1 = studentRepository.findById(student1.getId()).orElse(null);
        student2 = studentRepository.findById(student2.getId()).orElse(null);
        teacher = teacherRepository.findById(teacher.getId()).orElse(null);
        assertThat(student1.getDrivingLessons()).hasSize(1);
        assertThat(student2.getDrivingLessons()).hasSize(1);
        DrivingLesson drivingLesson1 = student1.getDrivingLessons().toArray(new DrivingLesson[0])[0];
        DrivingLesson drivingLesson2 = student2.getDrivingLessons().toArray(new DrivingLesson[0])[0];
        assertThat(drivingLesson1.getManualLesson()).isFalse();
        assertThat(drivingLesson2.getManualLesson()).isFalse();
        assertThat(drivingLesson1.getDriver().getId()).isEqualTo(student1.getId());
        assertThat(drivingLesson2.getDriver().getId()).isEqualTo(student2.getId());
        assertThat(drivingLesson1.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(drivingLesson2.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(drivingLesson1.getBegin()).isEqualTo(LocalDateTime.of(2020, 12, 16, 9, 15));
        assertThat(drivingLesson1.getEnd()).isEqualTo(LocalDateTime.of(2020, 12, 16, 10, 45));
        assertThat(drivingLesson2.getBegin()).isEqualTo(LocalDateTime.of(2020, 12, 16, 13, 0));
        assertThat(drivingLesson2.getEnd()).isEqualTo(LocalDateTime.of(2020, 12, 16, 14, 30));
        assertThat(drivingLesson1.getOptionalStudents()).hasSize(1);
        assertThat(drivingLesson2.getOptionalStudents()).hasSize(0);
        assertThat(drivingLesson1.getOptionalStudents().toArray(new Student[0])[0].getId()).isEqualTo(student2.getId());
        assertThat(drivingLesson1.getDriver().getLastScheduledTimeSlots()).hasSize(1);
        assertThat(drivingLesson1.getDriver().getLastScheduledTimeSlots().toArray(new TimeSlot[0])[0].getId()).isEqualTo(timeSlot1.getId());
        assertThat(drivingLesson2.getDriver().getLastScheduledTimeSlots()).hasSize(1);
        assertThat(drivingLesson2.getDriver().getLastScheduledTimeSlots().toArray(new TimeSlot[0])[0].getId()).isEqualTo(timeSlot2.getId());
        assertThat(student1.getDrivingLessons()).hasSize(1);
        assertThat(student1.getDrivingLessons()).contains(drivingLesson1);
        assertThat(student2.getDrivingLessons()).hasSize(1);
        assertThat(student2.getDrivingLessons()).contains(drivingLesson2);
        assertThat(student2.getLastScheduledTimeSlots()).hasSize(1);
        assertThat(teacher.getDrivingLessons()).hasSize(2);
        assertThat(teacher.getDrivingLessons()).contains(drivingLesson1, drivingLesson2);
    }

    @Transactional
    @Test
    public void createEmptyDrivingLessonsTest() {
        Teacher teacher = entityCreationHelper.createEntityTeacher(false);
        teacherRepository.save(teacher);
        Student student1 = entityCreationHelper.createEntityStudent(false);
        student1.getUser().setLogin("student1");
        Student student2 = entityCreationHelper.createEntityStudent(true);
        student2.getUser().setLogin("student2");
        studentRepository.save(student1);
        studentRepository.save(student2);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setTeacher(teacher);
        timeSlot.setDay(DayOfWeek.WE);
        timeSlot.setBegin(555);
        timeSlot.setEnd(645);
        timeSlotRepository.save(timeSlot);
        Preference preference = entityCreationHelper.createEntityPreference(false, false);
        preference.setStudent(student1);
        preference.setTimeSlot(timeSlot);
        preferenceRepository.save(preference);

        PlanningTeacher planningTeacher = new PlanningTeacher();
        planningTeacher.setId(teacher.getId());
        PlanningStudent s2 = new PlanningStudent();
        s2.setId(student2.getId());
        s2.setTeacher(planningTeacher);

        PlanningDrivingLesson lesson = new PlanningDrivingLesson();
        PlanningTimeSlot slot = new PlanningTimeSlot();
        slot.setId(timeSlot.getId());
        List<PlanningStudent> possibleStudents = new ArrayList<>();
        possibleStudents.add(s2);
        lesson.setPossibleStudents(possibleStudents);
        lesson.setSlot(slot);
        List<PlanningDrivingLesson> lessons = new ArrayList<>();
        lessons.add(lesson);

        Schedule solution = new Schedule(planningTeacher, null, lessons, new ArrayList<>(), 7, null);

        drivingLessonService.createDrivingLessons(solution, LocalDate.of(2020,12,14));

        List<DrivingLesson> drivingLessons = drivingLessonRepository.findAllByBeginEquals(LocalDateTime.of(2020,12,16,9,15));

        assertThat(drivingLessons).hasSize(1);
        DrivingLesson drivingLesson = drivingLessons.get(0);
        assertThat(drivingLesson.getManualLesson()).isFalse();
        assertThat(drivingLesson.getDriver()).isNull();
        assertThat(drivingLesson.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(drivingLesson.getBegin()).isEqualTo(LocalDateTime.of(2020, 12, 16, 9, 15));
        assertThat(drivingLesson.getEnd()).isEqualTo(LocalDateTime.of(2020, 12, 16, 10, 45));
        assertThat(drivingLesson.getOptionalStudents()).hasSize(1);
        assertThat(drivingLesson.getOptionalStudents().toArray(new Student[0])[0].getId()).isEqualTo(student2.getId());
        student1 = studentRepository.findById(student1.getId()).orElse(null);
        assertThat(student1.getDrivingLessons()).hasSize(0);
        assertThat(student1.getLastScheduledTimeSlots()).hasSize(0);
        student2 = studentRepository.findById(student2.getId()).orElse(null);
        assertThat(student2.getDrivingLessons()).hasSize(0);
        assertThat(student2.getLastScheduledTimeSlots()).hasSize(0);
        teacher = teacherRepository.findById(teacher.getId()).orElse(null);
        assertThat(teacher.getDrivingLessons()).hasSize(1);
        assertThat(teacher.getDrivingLessons()).contains(drivingLesson);
    }
}
