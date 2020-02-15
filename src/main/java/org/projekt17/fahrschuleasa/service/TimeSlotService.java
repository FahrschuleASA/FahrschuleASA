package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.TimeSlot;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.repository.PreferenceRepository;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.TimeSlotRepository;
import org.projekt17.fahrschuleasa.service.dto.TimeSlotDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class TimeSlotService {

    private final Logger log = LoggerFactory.getLogger(TimeSlotService.class);

    private final TimeSlotRepository timeSlotRepository;

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final PreferenceRepository preferenceRepository;

    private final MailService mailService;

    public TimeSlotService(TimeSlotRepository timeSlotRepository, TeacherRepository teacherRepository,
                           StudentRepository studentRepository, PreferenceRepository preferenceRepository,
                           MailService mailService){
        this.timeSlotRepository = timeSlotRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.preferenceRepository = preferenceRepository;
        this.mailService = mailService;
    }

    public Optional<TimeSlotDTO> createTimeSlot(TimeSlotDTO timeSlotDTO, String login) {
        TimeSlot timeSlot = new TimeSlot();
        return teacherRepository.findByUserLogin(login).map(teacher -> {
                timeSlot.setBegin(timeSlotDTO.getBegin());
                timeSlot.setEnd(timeSlotDTO.getEnd());
                timeSlot.setDay(timeSlotDTO.getDay());
                timeSlot.setPreferredCategories(timeSlotDTO.getPreferredCategories());
                timeSlot.setOptionalCategories(timeSlotDTO.getOptionalCategories());
                teacher.addTimeSlot(timeSlot);
                teacher.changedTimeSlots(true);
                log.info("Ein neuer Zeitslot wurde angelegt: {}", timeSlot);
                return new TimeSlotDTO(timeSlotRepository.save(timeSlot));
        });
    }

    public Optional<TimeSlotDTO> updateTimeSlot(TimeSlotDTO timeSlotDTO) {
        preferenceRepository.findAllByTimeSlotId(timeSlotDTO.getId()).forEach(preference -> {
            if (!timeSlotDTO.getOptionalCategories().contains(preference.getStudent().getCategory())
                && !timeSlotDTO.getPreferredCategories().contains(preference.getStudent().getCategory())) {
                preference.getStudent().setChangedPreferences(true);
                preference.getStudent().removePreference(preference);
                preferenceRepository.delete(preference);
            }
        });
        return this.timeSlotRepository.findById(timeSlotDTO.getId()).map(
            timeSlot -> {
                timeSlot.setPreferredCategories(timeSlotDTO.getPreferredCategories());
                timeSlot.setOptionalCategories(timeSlotDTO.getOptionalCategories());
                timeSlot.getTeacher().changedTimeSlots(true);
                log.info("Ein Zeitslot wurde aktualisiert: {}", timeSlot);
                return new TimeSlotDTO(timeSlot);
            });
    }

    public Optional<TimeSlotDTO> addBlockedDate(Long id, LocalDate blockedDate) {
        return timeSlotRepository.findById(id).map(timeSlot -> {
            timeSlot.getBlockedDates().add(blockedDate);
            log.info("Es wurde ein neues geblocktes Datum zu einem Zeitslot hinzugefügt: {}", timeSlot);
            return new TimeSlotDTO(timeSlot);
        });
    }

    public Optional<TimeSlotDTO> removeBlockedDate(Long id, LocalDate blockedDate) {
        return timeSlotRepository.findById(id).map(timeSlot -> {
            timeSlot.getBlockedDates().remove(blockedDate);
            log.info("Es wurde ein geblocktes Datum von einem Zeitslot entfernt: {}", timeSlot);
            return new TimeSlotDTO(timeSlot);
        });
    }

    @Scheduled(cron = "0 0 23 * * 2")
    public void removeOldBlockedDates() {
        LocalDate now = LocalDate.now().minusDays(2);
        timeSlotRepository.findAll().forEach(timeSlot -> timeSlot.setBlockedDates(timeSlot.getBlockedDates().stream()
            .filter(localDate -> localDate.isAfter(now)).collect(Collectors.toSet())));
    }

    public void deleteTimeSlot(Long id, String login) {
        preferenceRepository.findAllByTimeSlotId(id).forEach(preference -> {
            preference.getStudent().setChangedPreferences(true);
            preference.getStudent().removePreference(preference);
            preferenceRepository.delete(preference);
        });
        timeSlotRepository.deleteById(id);
        log.info("Es wurde ein Zeitslot entfernt");
        teacherRepository.findByUserLogin(login).orElseThrow(() -> new IllegalArgumentException(
            String.format("Could not update changedTimeSlots of Teacher %s, because the Teacher is not found", login)))
            .setChangedTimeSlots(true);
    }

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAllTimeSlotsForTeacherId(Long id) {
        List<TimeSlotDTO> res = timeSlotRepository.findAllByTeacherId(id).stream()
            .map(TimeSlotDTO::new).collect(Collectors.toList());
        return timeSlotRepository.findAllByTeacherId(id).stream()
            .map(TimeSlotDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAllTimeSlotsForTeacherLogin(String login) {
        return timeSlotRepository.findAllByTeacherUserLogin(login).stream()
            .map(TimeSlotDTO::new).collect(Collectors.toList());
    }

    private Set<DrivingCategory> categoryToSet(DrivingCategory drivingCategory) {
        Set<DrivingCategory> categories = new HashSet<>();
        categories.add(drivingCategory);
        return categories;
    }

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAllTimeSlotsForStudentLogin(String login) {
        return studentRepository.findByUserLogin(login)
            .map(student -> timeSlotRepository
                .findAllByTeacherIdAndOptionalCategoriesContainingOrPreferredCategoriesContaining(
                    student.getTeacher().getId(), categoryToSet(student.getCategory()),
                    categoryToSet(student.getCategory()))
                .stream().map(timeSlot -> {
                    TimeSlotDTO timeSlotDTO = new TimeSlotDTO(timeSlot);
                    timeSlotDTO.setBlockedDates(null);
                    return timeSlotDTO;
                }).collect(Collectors.toList())).orElse(new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public Optional<TimeSlotDTO> getTimeSlot(Long id, boolean isStudent) {
        if (isStudent)
            return this.timeSlotRepository.findById(id).map(timeSlot -> {
                TimeSlotDTO timeSlotDTO = new TimeSlotDTO(timeSlot);
                timeSlotDTO.setBlockedDates(null);
                return timeSlotDTO;
            });

        return this.timeSlotRepository.findById(id).map(TimeSlotDTO::new);
    }

    @Transactional(readOnly = true)
    public boolean isTimeSlotOfStudent(Long timeSlotId, String studentLogin) {
        return studentRepository.findByUserLogin(studentLogin)
            .map(student -> isTimeSlotOfTeacher(timeSlotId, student.getTeacher().getUser().getLogin()))
            .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isTimeSlotOfTeacher(Long timeSlotId, String teacherLogin) {
        return timeSlotRepository.findByIdAndTeacherUserLogin(timeSlotId, teacherLogin).isPresent();
    }

    @Transactional(readOnly = true)
    public void notifyStudentsAboutTimeSlotChanges(String login) {
        studentRepository.findAllByTeacherUserLoginAndUserActivatedIsTrue(login)
            .forEach(mailService::sendTimeSlotsChangedMail);
    }
}
