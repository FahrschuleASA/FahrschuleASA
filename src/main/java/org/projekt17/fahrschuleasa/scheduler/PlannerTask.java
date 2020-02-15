package org.projekt17.fahrschuleasa.scheduler;

import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.planner.Planner;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.service.DrivingLessonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class PlannerTask implements Runnable {

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final DrivingLessonService drivingLessonService;

    public PlannerTask(TeacherRepository teacherRepository, StudentRepository studentRepository,
                       DrivingLessonService drivingLessonService) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.drivingLessonService = drivingLessonService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void run() {
        List<Teacher> teachers = teacherRepository.findAll();

        //ENHANCEMENT find better approach to find start date
        LocalDate startDate = LocalDate.now();

        if (startDate.getDayOfWeek() == DayOfWeek.MONDAY && SchoolConfiguration.getCronExpression().getWeekday() != 1)
            startDate = startDate.plusWeeks(1);
        else
            startDate = startDate.plusDays(15 - startDate.getDayOfWeek().getValue());

        LocalDate finalStartDate = startDate;
        teachers.forEach(teacher -> {
            if (!teacher.isActive())
                return;
            Planner planner = new Planner(teacher, finalStartDate);
            drivingLessonService.createDrivingLessons(planner.makeSchedule(), finalStartDate);
        });
        teachers.forEach(teacher -> teacher.setChangedTimeSlots(false));
        studentRepository.findAllByUserActivatedIsTrue().forEach(student -> student.setChangedPreferences(false));
    }
}
