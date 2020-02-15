package org.projekt17.fahrschuleasa.planner;

import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.planner.problemFacts.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class ConstructionHelper {

    private Teacher teacher;

    /**
     * Due to circularity the student must be set manually after this method is called.
     * @param planningPreference
     * @return
     */
    private Preference createPreferenceFromPlanning(PlanningPreference planningPreference){
        Preference preference = new Preference();
        preference.setId(planningPreference.getId());
        preference.setPickup(createLocationFromPlanning(planningPreference.getPickup()));
        preference.setDestination(createLocationFromPlanning(planningPreference.getDestination()));

        long id = planningPreference.getSlot().getId();

        // search for preference time slot in teachers time slots
        for (TimeSlot timeSlot : teacher.getTimeSlots()){
            if (timeSlot.getId()!=id) continue;
            preference.setTimeSlot(timeSlot);
        }

        return preference;
    }

    public Preference createPreference(long id, Location pickup, Location destination, Student student, TimeSlot timeSlot){
        Preference preference = new Preference();
        preference.setId(id);
        preference.setPickup(pickup);
        preference.setDestination(destination);
        preference.setStudent(student);
        preference.setTimeSlot(timeSlot);
        return preference;
    }

    private Location createLocationFromPlanning (PlanningLocation planningLocation){
        Location location = new Location();
        location.setLatitude(planningLocation.getLatitude());
        location.setLongitude(planningLocation.getLongitude());
        return location;
    }

    public Location createLocation (Double longitude, Double latitude){
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private Student createStudentFromPlanning (PlanningStudent planningStudent){
        Student student = new Student();
        student.setId(planningStudent.getId());
        student.setActive(planningStudent.isActive());
        student.setCategory(planningStudent.getCategory());
        student.setWantedLessons(planningStudent.getWantedLessons());
        student.setChangedPreferences(planningStudent.isPreferencesChanged());
        student.setTeacher(teacher);

        for (PlanningPreference planningPreference : planningStudent.getPreferences()){
            Preference preference = createPreferenceFromPlanning(planningPreference);
            preference.setStudent(student);
            student.addPreference(preference);
        }

        return student;
    }

    public Student createStudent (long id, boolean active, DrivingCategory category,int wantedLessons, boolean preferencesChanged, Teacher teacher){
        Student student = new Student();
        student.setId(id);
        student.setActive(active);
        student.setCategory(category);
        student.setWantedLessons(wantedLessons);
        student.setChangedPreferences(preferencesChanged);
        student.setTeacher(teacher);
        student.setDrivingLessons(new HashSet<>());

        return student;
    }

    private TimeSlot createTimeSlotFromPlanning (PlanningTimeSlot planningTimeSlot){

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(planningTimeSlot.getId());
        timeSlot.setBegin(planningTimeSlot.getBegin());
        timeSlot.setEnd(planningTimeSlot.getEnd());
        timeSlot.setDay(DayOfWeek.values()[planningTimeSlot.getDay()]);
        timeSlot.setPreferredCategories(new HashSet<>(planningTimeSlot.getCategoryPreference()));
        timeSlot.setOptionalCategories(new HashSet<>(planningTimeSlot.getCategoryOptional()));

        return timeSlot;
    }

    public TimeSlot createTimeSlot (long id, int begin, int end, DayOfWeek day,
                                           Set<DrivingCategory> preferenceSet, Set<DrivingCategory> optionalSet,
                                           Set<LocalDate> blockedDates){
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(id);
        timeSlot.setBegin(begin);
        timeSlot.setEnd(end);
        timeSlot.setDay(day);
        timeSlot.setPreferredCategories(preferenceSet);
        timeSlot.setOptionalCategories(optionalSet);
        timeSlot.setBlockedDates(blockedDates);
        timeSlot.setTeacher(teacher);

        return timeSlot;
    }

    public Teacher createTeacherFromPlanning (PlanningTeacher planningTeacher) {
        teacher = new Teacher();
        teacher.setId(planningTeacher.getId());
        teacher.setActive(planningTeacher.isActive());
        teacher.setChangedTimeSlots(planningTeacher.isSlotsChanged());
        teacher.setSchoolOwner(planningTeacher.isOwner());

        Set<DrivingCategory> categories = new HashSet<>();
        categories.add(DrivingCategory.B);

        LocalDate yesterday = LocalDate.ofYearDay(2020,5);

        // add some driving lesson to test sunday rest time
        int end = 23*60+59 - planningTeacher.getSundayRestTime();
        int begin= Math.max(0,end-89);
        DrivingLesson lastDrivingLesson = new DrivingLesson();
        lastDrivingLesson.setTeacher(teacher);
        lastDrivingLesson.setBegin(LocalDateTime.of(yesterday.getYear(),yesterday.getMonth(),yesterday.getDayOfMonth(),begin/60,begin%60));
        lastDrivingLesson.setEnd(LocalDateTime.of(yesterday.getYear(),yesterday.getMonth(),yesterday.getDayOfMonth(),end/60,end%60));

        teacher.addDrivingLesson(lastDrivingLesson);

        // we need some student here because the backend iterates over all students to the last driving lesson
        Student lastStudent = new Student();
        lastStudent.addDrivingLessons(lastDrivingLesson);
        lastStudent.setActive(false);
        teacher.addStudent(lastStudent);


        for (PlanningTimeSlot planningTimeSlot : planningTeacher.getSlots()){
            TimeSlot timeSlot = createTimeSlotFromPlanning(planningTimeSlot);
            teacher.addTimeSlot(timeSlot);
        }

        for (PlanningStudent planningStudent : planningTeacher.getStudents()){
            Student student = createStudentFromPlanning(planningStudent);
            teacher.addStudent(student);
        }

        return teacher;
    }

    public Teacher createTeacher(long id, boolean active, boolean slotsChanged, boolean isOwner, Set<TimeSlot> timeSlots, Set<DrivingLesson> drivingLessons) {
        teacher = new Teacher();
        teacher.setId(id);
        teacher.setActive(active);
        teacher.setChangedTimeSlots(slotsChanged);
        teacher.setSchoolOwner(isOwner);
        teacher.setTimeSlots(timeSlots);
        teacher.setDrivingLessons(drivingLessons);

        return teacher;
    }

    public void addOldDrivingLesson (LocalDateTime begin, LocalDateTime end, Preference preference, Student student, TimeSlot slot, Teacher teacher){
        DrivingLesson drivingLesson = new DrivingLesson();
        drivingLesson.setTeacher(teacher);
        drivingLesson.setBegin(begin);
        drivingLesson.setEnd(end);
        drivingLesson.setDriver(student);
        drivingLesson.setPickup(preference.getPickup());
        drivingLesson.setDestination(preference.getDestination());
        teacher.addDrivingLesson(drivingLesson);
        student.addDrivingLessons(drivingLesson);
        student.addLastScheduledTimeSlots(slot);
    }

    public void addManualDrivingLesson (LocalDateTime begin, LocalDateTime end, Preference preference, Student student, TimeSlot slot, Teacher teacher){
        DrivingLesson drivingLesson = new DrivingLesson();
        drivingLesson.setTeacher(teacher);
        drivingLesson.setBegin(begin);
        drivingLesson.setEnd(end);
        drivingLesson.setDriver(student);
        drivingLesson.setPickup(preference.getPickup());
        drivingLesson.setDestination(preference.getDestination());
        teacher.addDrivingLesson(drivingLesson);
        student.addDrivingLessons(drivingLesson);
        student.addLastScheduledTimeSlots(slot);

        drivingLesson.setManualLesson(true);
    }

    public TimeSlot findTimeSlotById(Teacher teacher, long id) {
        for (TimeSlot timeSlot : teacher.getTimeSlots()) {
            if (timeSlot.getId() == id) {
                return timeSlot;
            }
        }
        return null;
    }

    public Student findStudentById(Teacher teacher, long id) {
        for (Student student : teacher.getStudents()) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    public void addScheduledDrivingLessons(Teacher teacher, LocalDate baseDate, Schedule schedule){
            for (PlanningDrivingLesson lesson : schedule.getAllLessons()) {
                if (!lesson.isManualLesson()) {

                    DrivingLesson drivingLesson = new DrivingLesson().manualLesson(false);
                    teacher.addDrivingLesson(drivingLesson);

                    TimeSlot timeSlot = findTimeSlotById(teacher, lesson.getId());

                    LocalDate date = baseDate.plusDays(timeSlot.getDay().ordinal());
                    LocalDateTime begin = LocalDateTime.of(date, LocalTime.of(timeSlot.getBegin() / 60, timeSlot.getBegin() % 60));
                    if (timeSlot.getEnd() < timeSlot.getBegin())
                        date = date.plusDays(1);
                    LocalDateTime end = LocalDateTime.of(date, LocalTime.of(timeSlot.getEnd() / 60, timeSlot.getEnd() % 60));
                    drivingLesson.setBegin(begin);
                    drivingLesson.setEnd(end);

                    if (lesson.getStudentId() != null) {
                        Student student = findStudentById(teacher, lesson.getStudentId());

                        student.addDrivingLessons(drivingLesson);
                        student.addLastScheduledTimeSlots(timeSlot);
                    }
                }
            }
        }

    }

