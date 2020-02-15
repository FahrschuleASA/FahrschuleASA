package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.config.PlannerConfiguration;
import org.projekt17.fahrschuleasa.config.SchoolConfiguration;
import org.projekt17.fahrschuleasa.domain.*;
import org.projekt17.fahrschuleasa.domain.enumeration.DayOfWeek;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingLessonType;
import org.projekt17.fahrschuleasa.planner.Schedule;
import org.projekt17.fahrschuleasa.repository.*;
import org.projekt17.fahrschuleasa.service.dto.DrivingLessonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DrivingLessonService {

    private final Logger log = LoggerFactory.getLogger(DrivingLessonService.class);

    private final DrivingLessonRepository drivingLessonRepository;

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    private final TimeSlotRepository timeSlotRepository;

    private final PreferenceRepository preferenceRepository;

    private final MailService mailService;

    private final LocationService locationService;

    private final int MAX_TIME_OWNER = PlannerConfiguration.getMaximalTimeOwner();

    private final int MAX_TIME_TEACHER = PlannerConfiguration.getMaximalTimeTeacher();

    private final int MINIMAL_REST_TIME = PlannerConfiguration.getMinimalRestTime();

    public DrivingLessonService(DrivingLessonRepository drivingLessonRepository, StudentRepository studentRepository,
                                TeacherRepository teacherRepository, TimeSlotRepository timeSlotRepository,
                                PreferenceRepository preferenceRepository, MailService mailService,
                                LocationService locationService) {
        this.drivingLessonRepository = drivingLessonRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.preferenceRepository = preferenceRepository;
        this.mailService = mailService;
        this.locationService = locationService;
    }

    public void cancelDrivingLesson(Long drivingLessonId, String login, boolean teacherRequest, boolean bookable) {
        DrivingLesson drivingLesson = drivingLessonRepository.findById(drivingLessonId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("DrivingLesson %d not found", drivingLessonId)));
        Student student = drivingLesson.getDriver();

        if (!teacherRequest && !student.getUser().getLogin().equals(login))
            throw new IllegalStateException(String.format("Student %s cannot cancel DrivingLesson %d of Student %s",
                login, drivingLesson.getId(), student.getUser().getLogin()));

        LocalDateTime now = LocalDateTime.now();

        if (drivingLesson.getEnd().isBefore(now))
            throw new IllegalStateException("We cannot cancel a DrivingLesson that is already over!");
            //throw new BadRequestAlertException("We cannot cancel a DrivingLesson that is already over!", "drivingLesson", "pastcancel");

        if (!teacherRequest && now.until(drivingLesson.getBegin(), ChronoUnit.HOURS) < SchoolConfiguration.getDeadlineMissedLesson())
            student.addLateMissedLessons(drivingLesson);
        else
            student.addMissedLessons(drivingLesson);

        student.removeDrivingLessons(drivingLesson);
        drivingLesson.setPickup(null);
        drivingLesson.setDestination(null);
        drivingLesson.setLessonType(DrivingLessonType.NORMAL);

        if (teacherRequest) {
            drivingLesson.setBookable(bookable);
            log.info("Die Fahrstunde am {} von {} bis {} Uhr wurde von einem Administrator oder Fahrlehrer abgesagt",
                drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")),
                drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        else
            log.info("Die Fahrstunde am {} von {} bis {} Uhr wurde von dem Fahrschüler {} abgesagt",
                drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")),
                drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")),
                student.getUser().getFirstName() + " " + student.getUser().getLastName());

        mailService.sendCancelDrivingLessonTeacherMail(student.getTeacher(), student, drivingLesson, teacherRequest, bookable);
        mailService.sendCancelDrivingLessonStudentMail(student, drivingLesson, !teacherRequest, false);
        if (bookable)
            drivingLesson.getOptionalStudents().forEach(optionalStudent -> {
                if (optionalStudent.getNotifyForFreeLesson())
                    mailService.sendCancelDrivingLessonStudentMail(student, drivingLesson, false, true);
            });
    }

    private int computeDurationOnDay(LocalDateTime begin, LocalDateTime end, LocalDate day) {
        if (begin.toLocalDate().isEqual(end.toLocalDate()))
            return (int) ChronoUnit.MINUTES.between(begin, end);
        else {
            if (begin.toLocalDate().isEqual(day))
                return (int) ChronoUnit.MINUTES.between(begin, LocalDateTime.of(day.plusDays(1), LocalTime.MIDNIGHT));
            else
                return (int) ChronoUnit.MINUTES.between(LocalDateTime.of(day, LocalTime.MIDNIGHT), end);
        }
    }

    private boolean checkMaxTime(DrivingLessonDTO drivingLessonDTO, Long teacherId) {
        int maximalTimePerDay;
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(IllegalArgumentException::new);
        if (teacher.isSchoolOwner())
            maximalTimePerDay = MAX_TIME_OWNER;
        else
            maximalTimePerDay = MAX_TIME_TEACHER;

        LocalDateTime begin = drivingLessonDTO.getBegin();
        LocalDateTime end = drivingLessonDTO.getEnd();
        LocalDate beginDate = begin.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        List<DrivingLesson> drivingLessonsBeginDay = drivingLessonRepository.findAllByDriverIsNotNullAndTeacherIdAndBeginGreaterThanEqualAndBeginBefore(teacherId,
            LocalDateTime.of(beginDate, LocalTime.MIDNIGHT), LocalDateTime.of(beginDate.plusDays(1), LocalTime.MIDNIGHT));

        if (beginDate.isEqual(endDate)) {
            int timeOfDay = computeDurationOnDay(begin, end, beginDate);
            for (DrivingLesson lesson : drivingLessonsBeginDay)
                timeOfDay += computeDurationOnDay(lesson.getBegin(), lesson.getEnd(), beginDate);
            return timeOfDay < maximalTimePerDay;
        } else {
            List<DrivingLesson> drivingLessonsEndDay = drivingLessonRepository.findAllByDriverIsNotNullAndTeacherIdAndBeginGreaterThanEqualAndBeginBefore(teacherId,
                LocalDateTime.of(endDate, LocalTime.MIDNIGHT), LocalDateTime.of(endDate.plusDays(1), LocalTime.MIDNIGHT));

            int timeOfBeginDay = computeDurationOnDay(begin, end, beginDate);
            for (DrivingLesson lesson : drivingLessonsBeginDay)
                timeOfBeginDay += computeDurationOnDay(lesson.getBegin(), lesson.getEnd(), beginDate);

            int timeOfEndDay = computeDurationOnDay(begin, end, endDate);
            for (DrivingLesson lesson : drivingLessonsEndDay)
                timeOfEndDay += computeDurationOnDay(lesson.getBegin(), lesson.getEnd(), endDate);

            return timeOfBeginDay < maximalTimePerDay && timeOfEndDay < maximalTimePerDay;
        }
    }

    private boolean checkRestTime(DrivingLessonDTO drivingLessonDTO, Long teacherId) {
        LocalDateTime begin = drivingLessonDTO.getBegin();
        LocalDateTime end = drivingLessonDTO.getEnd();
        LocalDate beginDate = begin.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        LocalTime cutTime = LocalTime.of(6,0);
        if (end.toLocalTime().equals(cutTime) || end.toLocalTime().isBefore(cutTime)) {
            List<DrivingLesson> drivingLessonsAfterCut = drivingLessonRepository.findAllByDriverIsNotNullAndTeacherIdAndBeginGreaterThanEqualAndBeginBeforeOrderByBeginAsc(teacherId,
                LocalDateTime.of(endDate, cutTime), LocalDateTime.of(endDate.plusDays(1), LocalTime.MIDNIGHT));
            if (drivingLessonsAfterCut.isEmpty()) return true;
            return ChronoUnit.MINUTES.between(end, drivingLessonsAfterCut.get(0).getBegin()) >= MINIMAL_REST_TIME;
        } else {
            List<DrivingLesson> drivingLessonsBeforeCut = drivingLessonRepository.findAllByDriverIsNotNullAndTeacherIdAndBeginAfterAndEndLessThanEqualOrderByEndDesc(teacherId,
                begin.minusDays(1), LocalDateTime.of(beginDate, cutTime));
            List<DrivingLesson> drivingLessonsAfterCutNextDay = drivingLessonRepository.findAllByDriverIsNotNullAndTeacherIdAndBeginGreaterThanEqualAndBeginBeforeOrderByBeginAsc(teacherId,
                LocalDateTime.of(beginDate.plusDays(1), cutTime), end.plusDays(1));

            int restTimeBefore;
            if (drivingLessonsBeforeCut.isEmpty())
                restTimeBefore = MINIMAL_REST_TIME;
            else
                restTimeBefore = (int) ChronoUnit.MINUTES.between(drivingLessonsBeforeCut.get(0).getEnd(), begin);

            int restTimeAfter;
            if (drivingLessonsAfterCutNextDay.isEmpty())
                restTimeAfter = MINIMAL_REST_TIME;
            else
                restTimeAfter = (int) ChronoUnit.MINUTES.between(end, drivingLessonsAfterCutNextDay.get(0).getBegin());

            return restTimeBefore >= MINIMAL_REST_TIME && restTimeAfter >= MINIMAL_REST_TIME;
        }
    }

    private boolean checkValid(DrivingLessonDTO drivingLessonDTO, Teacher teacher) {
        Long teacherId;
        if (drivingLessonDTO.getTeacherId() != null)
            teacherId = drivingLessonDTO.getTeacherId();
        else {
            if (teacher != null)
                teacherId = teacher.getId();
            else
                throw new IllegalArgumentException("Teacher Id is null");
        }
        return checkMaxTime(drivingLessonDTO, teacherId) && checkRestTime(drivingLessonDTO, teacherId);
    }

    public DrivingLessonDTO bookDrivingLesson(DrivingLessonDTO drivingLessonDTO, String login, boolean teacherRequest) {
        if (!checkValid(drivingLessonDTO, null))
            throw new IllegalStateException("This action breaks legal requirements");
            //throw new BadRequestAlertException("This action breaks legal requirements", "drivingLesson", "badconditions");
        DrivingLesson drivingLesson = drivingLessonRepository.findById(drivingLessonDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("DrivingLesson %d not found", drivingLessonDTO.getId())));
        if (drivingLesson.getDriver() != null)
            throw new IllegalStateException(String.format("DrivingLesson is already booked by Student %d", drivingLesson.getDriver().getId()));
            //throw new BadRequestAlertException(String.format("DrivingLesson is already booked by Student %d", drivingLesson.getDriver().getId()),
            //    "drivingLesson", "alreadybooked");
        if (drivingLesson.getEnd().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("We cannot book a DrivingLesson that is already over!");
            //throw new BadRequestAlertException("We cannot book a DrivingLesson that is already over!", "drivingLesson", "pastbook");
        if (!drivingLesson.getBookable())
            throw new IllegalStateException("We cannot book a DrivingLesson that is not bookable!");
            //throw new BadRequestAlertException("We cannot book a DrivingLesson that is not bookable!", "drivingLesson", "notbookable");

        Student student;

        if (teacherRequest)
            student = studentRepository.findById(drivingLessonDTO.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Student %d not found", drivingLessonDTO.getDriverId())));
        else
            student = studentRepository.findByUserLogin(login)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Student %s not found", login)));

        if (!student.getTeacher().getId().equals(drivingLesson.getTeacher().getId()))
            throw new IllegalStateException(String.format("Driving lesson could not be booked by Student %d " +
                    "of Teacher %d, because it is a driving lesson of Teacher %d", student.getId(), student.getTeacher().getId(),
                drivingLesson.getTeacher().getId()));
            //throw new BadRequestAlertException(String.format("Driving lesson could not be booked by Student %d " +
            //        "of Teacher %d, because it is a driving lesson of Teacher %d", student.getId(), student.getTeacher().getId(),
            //    drivingLesson.getTeacher().getId()), "drivingLesson", "wrongteacher");

        drivingLesson.setPickup(locationService.createLocation(drivingLessonDTO.getPickup()));
        drivingLesson.setDestination(locationService.createLocation(drivingLessonDTO.getDestination()));

        student.addDrivingLessons(drivingLesson);

        mailService.sendBookDrivingLessonTeacherMail(student.getTeacher(), student, drivingLesson, teacherRequest);
        mailService.sendBookDrivingLessonStudentMail(student, drivingLesson, !teacherRequest);

        log.info("Die Fahrstunde am {} von {} bis {} Uhr wurde von {} gebucht oder diesem durch einen Administrator oder Fahrlehrer zugeordnet",
            drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")),
            drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")),
            student.getUser().getFirstName() + " " + student.getUser().getLastName());

        return new DrivingLessonDTO(drivingLesson);
    }

    private DrivingLessonDTO addDrivingLesson(DrivingLessonDTO drivingLessonDTO, Teacher teacher) {
        if (!checkValid(drivingLessonDTO, teacher))
            throw new IllegalStateException("This action breaks legal requirements");
            //throw new BadRequestAlertException("This action breaks legal requirements", "drivingLesson", "badconditions");
        if (drivingLessonDTO.getBegin().getDayOfWeek().equals(drivingLessonDTO.getEnd().getDayOfWeek()))
            timeSlotRepository.findAllTimeSlotsWithoutDayChange(
                drivingLessonDTO.getEnd().getHour() * 60 + drivingLessonDTO.getEnd().getMinute(),
                drivingLessonDTO.getBegin().getHour() * 60 + drivingLessonDTO.getBegin().getMinute(),
                DayOfWeek.values()[drivingLessonDTO.getBegin().getDayOfWeek().getValue()-1],
                DayOfWeek.values()[Math.floorMod(drivingLessonDTO.getBegin().getDayOfWeek().getValue()-2, 7)],
                teacher.getId())
                .forEach(timeSlot -> {
                    if (timeSlot.getDay().equals(DayOfWeek.values()[drivingLessonDTO.getBegin().getDayOfWeek().getValue()-1]))
                        timeSlot.getBlockedDates().add(drivingLessonDTO.getBegin().toLocalDate());
                    else
                        timeSlot.getBlockedDates().add(drivingLessonDTO.getBegin().toLocalDate().minusDays(1));
                });
        else
            timeSlotRepository.findAllTimeSlotsWithDayChange(
                drivingLessonDTO.getEnd().getHour() * 60 + drivingLessonDTO.getEnd().getMinute(),
                drivingLessonDTO.getBegin().getHour() * 60 + drivingLessonDTO.getBegin().getMinute(),
                DayOfWeek.values()[drivingLessonDTO.getBegin().getDayOfWeek().getValue()-1],
                DayOfWeek.values()[drivingLessonDTO.getEnd().getDayOfWeek().getValue()-1], teacher.getId()).forEach(timeSlot -> {
                    if (timeSlot.getDay().equals(DayOfWeek.values()[drivingLessonDTO.getBegin().getDayOfWeek().getValue()-1]))
                        timeSlot.getBlockedDates().add(drivingLessonDTO.getBegin().toLocalDate());
                    else
                        timeSlot.getBlockedDates().add(drivingLessonDTO.getEnd().toLocalDate());
            });

        Student student = null;
        if (drivingLessonDTO.getDriverId() != null) {
            student = studentRepository.findById(drivingLessonDTO.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Student %d not found", drivingLessonDTO.getDriverId())));
            if (!student.getTeacher().getId().equals(teacher.getId()))
                throw new IllegalStateException("The teacher of the student must coincide with the teacher of the driving lesson");
                //throw new BadRequestAlertException("The teacher of the student must coincide with the teacher of the driving lesson",
                //    "drivingLesson", "teachermismatch");
        }

        DrivingLesson drivingLesson = new DrivingLesson().manualLesson(true);

        drivingLesson.setTeacher(teacher);
        drivingLesson.setDriver(student);
        drivingLesson.setBegin(drivingLessonDTO.getBegin());
        drivingLesson.setEnd(drivingLessonDTO.getEnd());
        drivingLesson.setPickup(locationService.createLocation(drivingLessonDTO.getPickup()));
        drivingLesson.setDestination(locationService.createLocation(drivingLessonDTO.getDestination()));
        if (drivingLessonDTO.getBookable() == null)
            drivingLesson.setBookable(true);
        else
            drivingLesson.setBookable(drivingLessonDTO.getBookable());

        log.info("Es wurde eine neue Fahrstunde am {} von {} bis {} Uhr manuell erstellt",
            drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")),
            drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));

        return new DrivingLessonDTO(drivingLessonRepository.save(drivingLesson));
    }

    public Optional<DrivingLessonDTO> addDrivingLesson(DrivingLessonDTO drivingLessonDTO) {
        return teacherRepository.findById(drivingLessonDTO.getTeacherId())
            .map(teacher -> addDrivingLesson(drivingLessonDTO, teacher));
    }

    public Optional<DrivingLessonDTO> addDrivingLesson(DrivingLessonDTO drivingLessonDTO, String login) {
        return teacherRepository.findByUserLogin(login).map(teacher -> addDrivingLesson(drivingLessonDTO, teacher));
    }

    public void createDrivingLessons(Schedule schedule, LocalDate baseDate) {
        if (schedule.getAllLessons().isEmpty())
            return;

        studentRepository.findAllByTeacherId(schedule.getTeacher().getId()).forEach(student -> student.setLastScheduledTimeSlots(new HashSet<>()));

        Teacher teacher = teacherRepository.findById(schedule.getTeacher().getId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown teacher %d", schedule.getTeacher().getId())));

        schedule.getAllLessons().forEach(lesson -> {

            //if DrivingLesson was manually created and therefor already persisted in the database,
            // we have nothing to do here and continue with the next lesson
            if (lesson.isManualLesson())
                return;

            //create new DrivingLesson
            DrivingLesson drivingLesson = new DrivingLesson().manualLesson(false);

            teacher.addDrivingLesson(drivingLesson);

            //get the begin and end date for this DrivingLesson
            TimeSlot timeSlot = timeSlotRepository.findById(lesson.getSlot().getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown TimeSlot %d", lesson.getSlot().getId())));
            LocalDate date = baseDate.plusDays(timeSlot.getDay().ordinal());
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.of(timeSlot.getBegin() / 60, timeSlot.getBegin() % 60));
            if (timeSlot.getEnd() < timeSlot.getBegin())
                date = date.plusDays(1);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.of(timeSlot.getEnd() / 60, timeSlot.getEnd() % 60));
            drivingLesson.setBegin(begin);
            drivingLesson.setEnd(end);

            //get the optional students for this DrivingLesson
            lesson.getPossibleStudents().forEach(s -> {
                if (!Long.valueOf(s.getId()).equals(lesson.getStudentId()))
                    drivingLesson.addOptionalStudents(studentRepository.findById(s.getId())
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown student %d", s.getId()))));
            });

            if (lesson.getStudentId() != null) {
                //get the student who is assigned to this DrivingLesson
                Student student = studentRepository.findById(lesson.getStudentId()).orElseThrow(
                    () -> new IllegalArgumentException(String.format("Unknown student %d", lesson.getStudentId())));

                //get the pickup and destination locations of the student for this DrivingLesson
                Preference preference = preferenceRepository
                    .findByStudentIdAndTimeSlotId(lesson.getStudentId(), lesson.getSlot().getId()).orElseThrow(
                        () -> new IllegalArgumentException(String.format("Preference for student %d and time slot %d not found",
                            lesson.getStudentId(), lesson.getSlot().getId())));
                drivingLesson.setPickup(preference.getPickup());
                drivingLesson.setDestination(preference.getDestination());

                student.addDrivingLessons(drivingLesson);

                student.addLastScheduledTimeSlots(preference.getTimeSlot());
            }
        });
    }

    public Optional<DrivingLessonDTO> updateDrivingLesson(DrivingLessonDTO drivingLessonDTO) {
        return drivingLessonRepository.findById(drivingLessonDTO.getId()).map(drivingLesson -> {
            drivingLesson.setLessonType(drivingLessonDTO.getLessonType());
            drivingLesson.setPickup(locationService.createLocation(drivingLessonDTO.getPickup()));
            drivingLesson.setDestination(locationService.createLocation(drivingLessonDTO.getDestination()));
            drivingLesson.setBookable(drivingLessonDTO.getBookable());
            log.info("Die Informationen der Fahrstunde am {} von {} bis {} Uhr wurden geändert: {}",
                drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                drivingLesson.getBegin().format(DateTimeFormatter.ofPattern("HH:mm")),
                drivingLesson.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")),
                drivingLesson);
            return new DrivingLessonDTO(drivingLesson);
        });
    }

    @Scheduled(cron = "0 0 23 * * 3")
    public void removeOrphanDrivingLessons() {
        LocalDateTime now = LocalDateTime.now();
        drivingLessonRepository.findAllByDriverIsNullAndBeginAfterAndEndBefore(now.minusDays(10), now)
            .forEach(drivingLesson -> {
                if (drivingLesson.getMissingStudents().isEmpty() && drivingLesson.getLateMissingStudents().isEmpty())
                    drivingLessonRepository.delete(drivingLesson);
            });
    }

    @Transactional(readOnly = true)
    public Optional<List<DrivingLessonDTO>> getAllDrivingLessonsByStudentId(Long id) {
        return studentRepository.findById(id).map(student -> student.getDrivingLessons().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Optional<List<DrivingLessonDTO>> getAllDrivingLessonsByStudentLogin(String login) {
        return studentRepository.findByUserLogin(login).map(student -> student.getDrivingLessons().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllUnassignedDrivingLessonsByTeacherId(Long id) {
        return drivingLessonRepository.findAllByDriverIsNullAndTeacherIdAndBeginGreaterThan(id, LocalDateTime.now())
            .stream().map(DrivingLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllUnassignedDrivingLessons() {
        return drivingLessonRepository.findAllByDriverIsNullAndBeginGreaterThan(LocalDateTime.now())
            .stream().map(DrivingLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllUnassignedDrivingLessonsByTeacherLogin(String login) {
        return drivingLessonRepository.findAllByDriverIsNullAndTeacherUserLoginAndBeginGreaterThan(login, LocalDateTime.now())
            .stream().map(DrivingLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllUnassignedDrivingLessonsByStudentLogin(String login) {
        return studentRepository.findByUserLogin(login)
            .map(student -> {
                if (student.getTeacher() == null)
                    return new ArrayList<DrivingLessonDTO>();
                return drivingLessonRepository.findAllByDriverIsNullAndTeacherIdAndBeginGreaterThanAndBookableIsTrue(
                    student.getTeacher().getId(), LocalDateTime.now())
                    .stream().map(DrivingLessonDTO::new).collect(Collectors.toList());
            }).orElse(new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public boolean isDrivingLessonOfStudent(String login, Long drivingLessonId) {
        return drivingLessonRepository.findByIdAndDriverNotNullAndDriverUserLogin(drivingLessonId, login).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<DrivingLessonDTO> getDrivingLessonById(Long id) {
        return drivingLessonRepository.findById(id).map(DrivingLessonDTO::new);
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllAssignedDrivingLessonsByTeacherId(Long teacherId) {
        return drivingLessonRepository.findAllByDriverIsNotNullAndTeacherId(teacherId).stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllAssignedDrivingLessonsByTeacherLogin(String login) {
        return drivingLessonRepository.findAllByDriverIsNotNullAndTeacherUserLogin(login).stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrivingLessonDTO> getAllAssignedDrivingLessons() {
        return drivingLessonRepository.findAllByDriverIsNotNull().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<List<DrivingLessonDTO>> getAllCanceledDrivingLessonsByStudentId(Long id) {
        return studentRepository.findById(id).map(student -> student.getMissedLessons().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Optional<List<DrivingLessonDTO>> getAllCanceledDrivingLessonsByStudentLogin(String login) {
        return studentRepository.findByUserLogin(login).map(student -> student.getMissedLessons().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Optional<List<DrivingLessonDTO>> getAllLateCanceledDrivingLessonsByStudentId(Long id) {
        return studentRepository.findById(id).map(student -> student.getLateMissedLessons().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Optional<List<DrivingLessonDTO>> getAllLateCanceledDrivingLessonsByStudentLogin(String login) {
        return studentRepository.findByUserLogin(login).map(student -> student.getLateMissedLessons().stream()
            .map(DrivingLessonDTO::new).collect(Collectors.toList()));
    }
}
