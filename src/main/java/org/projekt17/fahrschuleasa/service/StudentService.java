package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.domain.enumeration.DrivingCategory;
import org.projekt17.fahrschuleasa.repository.PreferenceRepository;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.SmallStudentDTO;
import org.projekt17.fahrschuleasa.service.dto.StudentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    private final PreferenceRepository preferenceRepository;

    private final UserService userService;

    private final MailService mailService;

    private final LocationService locationService;

    public StudentService(StudentRepository studentRepository, TeacherRepository teacherRepository,
                          PreferenceRepository preferenceRepository, UserService userService, MailService mailService,
                          LocationService locationService) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.preferenceRepository = preferenceRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.locationService = locationService;
    }

    public StudentDTO createStudent(StudentDTO studentDTO){
        logger.debug("created new Student: {}", studentDTO);

        Student newStudent = new Student();

        Set<String> authorities = new HashSet<>();
        authorities.add(AuthoritiesConstants.USER);
        authorities.add(AuthoritiesConstants.STUDENT);
        studentDTO.getUser().setAuthorities(authorities);

        userService.createMyAccount(newStudent, studentDTO);

        newStudent.setCategory(studentDTO.getCategory());
        Teacher studentsTeacher = teacherRepository.findById(studentDTO.getTeacherId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("Teacher %d not found",
                studentDTO.getTeacherId())));
        newStudent.setTeacher(studentsTeacher);
        newStudent.setTeachingDiagram(new TeachingDiagram());

        studentRepository.save(newStudent);
        mailService.sendCreationEmail(newStudent.getUser());

        logger.debug("Created Information for Student: {}", newStudent);
        logger.info("Ein neuer Fahrschüler wurde angelegt: {}", newStudent);
        return new StudentDTO(newStudent);

    }

    /**
     * Update all information for a given student
     * @param studentDTO student to update
     */
    public Optional<StudentDTO> updateStudent(StudentDTO studentDTO) {
        return studentRepository.findById(studentDTO.getId()).map(student -> {
            updateDrivingCategory(student, studentDTO.getCategory());
            student.setTeacher(teacherRepository.findById(studentDTO.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("teacher %d not found",
                    studentDTO.getTeacherId()))));
            student.setBirthdate(studentDTO.getBirthdate());
            student.setPhoneNumber(studentDTO.getPhoneNumber());
            student.setAddress(locationService.createLocation(studentDTO.getAddress()));

            userService.updateEmail(studentDTO, student);

            User user = student.getUser();
            user.setFirstName(studentDTO.getUser().getFirstName());
            user.setLastName(studentDTO.getUser().getLastName());
            user.setActivated(studentDTO.getUser().isActivated());

            logger.info("Die Informationen eines Fahrschülers wurde geändert: {}", student);

            return new StudentDTO(student);
        });
    }

    public void updateDrivingCategory(Student student, DrivingCategory drivingCategory) {
        if (!student.getCategory().equals(drivingCategory)) {
            student.setCategory(drivingCategory);
            student.setChangedPreferences(true);
            preferenceRepository.findAllByStudentId(student.getId()).forEach(preference -> {
                if (!preference.getTimeSlot().getOptionalCategories().contains(student.getCategory())
                    && !preference.getTimeSlot().getPreferredCategories().contains(student.getCategory())) {
                    student.removePreference(preference);
                    preferenceRepository.delete(preference);
                }
            });
            mailService.sendCategoryChangedMail(student);
        }
    }

    public Optional<StudentDTO> updateStudentSettings(StudentDTO studentDTO, String login) {
        return studentRepository.findByUserLogin(login).map(student -> {
            student.setReadyForTheory(studentDTO.getReadyForTheory());
            student.setNotifyForFreeLesson(studentDTO.getNotifyForFreeLesson());
            userService.setActiveStatus(studentDTO, student);
            student.setWantedLessons(studentDTO.getWantedLessons() > student.getAllowedLessons()
                ? student.getAllowedLessons() : studentDTO.getWantedLessons());
            logger.info("Die Einstellungen eines Fahrschülers haben sich geändert: {}", student);
            return new StudentDTO(student);
        });
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void setStudentsActive() {
        studentRepository.findAllByDeactivatedUntilIsNotNullAndDeactivatedUntilLessThanEqual(LocalDate.now())
            .forEach(student -> {
                student.setDeactivatedUntil(null);
                student.setActive(true);
            });
    }

    public Optional<MyAccountDTO> updateStudentMyAccountSettings(MyAccountDTO myAccountDTO, String login) {
        return studentRepository.findByUserLogin(login).map(student -> {
            student.setPhoneNumber(myAccountDTO.getPhoneNumber());
            userService.updateEmail(myAccountDTO, student);
            return new MyAccountDTO(student);
        });
    }

    public String deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Student %d not found", studentId)));
        student.getUser().setActivated(false);
        student.setDeactivatedUntil(null);
        student.setActive(false);
        userService.clearUserCaches(student.getUser());
        logger.info("Der Fahrschüler {} wurde deaktiviert",
            student.getUser().getFirstName() + " " + student.getUser().getLastName());
        return student.getUser().getFirstName() + " " + student.getUser().getLastName();
    }

    @Transactional(readOnly = true)
    public Optional<StudentDTO> getStudentByLogin(String login) {
        return studentRepository.findByUserLogin(login).map(StudentDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<StudentDTO> getStudentById(Long id) {
        return studentRepository.findById(id).map(StudentDTO::new);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents(boolean activeOnly) {
        if (activeOnly)
            return studentRepository.findAllByUserActivatedIsTrue().stream()
                .map(StudentDTO::new).collect(Collectors.toList());

        return studentRepository.findAll().stream().map(StudentDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SmallStudentDTO> getAllStudentsSmall(boolean activeOnly) {
        if (activeOnly)
            return studentRepository.findAllByUserActivatedIsTrue().stream()
                .map(SmallStudentDTO::new).collect(Collectors.toList());

        return studentRepository.findAll().stream().map(SmallStudentDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudentsForTeacher(String login, boolean activeOnly) {
        if (activeOnly)
            return studentRepository.findAllByTeacherUserLoginAndUserActivatedIsTrue(login).stream()
                .map(StudentDTO::new).collect(Collectors.toList());

        return studentRepository.findAllByTeacherUserLogin(login).stream()
            .map(StudentDTO::new).collect(Collectors.toList());
    }
}
