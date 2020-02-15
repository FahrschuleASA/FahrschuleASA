package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.Teacher;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.TeacherDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class TeacherService {

    private final Logger log = LoggerFactory.getLogger(TeacherService.class);

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final UserService userService;

    private final MailService mailService;

    public TeacherService(TeacherRepository teacherRepository, UserService userService,
                          StudentRepository studentRepository, MailService mailService) {
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        this.studentRepository = studentRepository;
        this.mailService = mailService;
    }

    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {

        Teacher newTeacher = new Teacher();

        Set<String> authorities = new HashSet<>();
        authorities.add(AuthoritiesConstants.USER);
        authorities.add(AuthoritiesConstants.TEACHER);
        teacherDTO.getUser().setAuthorities(authorities);

        userService.createMyAccount(newTeacher, teacherDTO);

        newTeacher.setSchoolOwner(teacherDTO.getSchoolOwner());

        teacherRepository.save(newTeacher);
        mailService.sendCreationEmail(newTeacher.getUser());

        log.debug("Created Information for Teacher: {}", newTeacher);
        log.info("Ein neuer Fahrlehrer wurde angelegt: {}", newTeacher);
        return new TeacherDTO(newTeacher);
    }

    public MyAccountDTO updateTeacherSettings(MyAccountDTO myAccountDTO, Teacher teacher) {
        userService.setActiveStatus(myAccountDTO, teacher);
        log.info("Die Einstellungen eines Fahrlehrers haben sich geändert: {}", teacher);
        return new MyAccountDTO(teacher);
    }

    public Optional<MyAccountDTO> updateTeacherSettings(MyAccountDTO myAccountDTO, String login) {
        return teacherRepository.findByUserLogin(login).map(teacher -> updateTeacherSettings(myAccountDTO, teacher));
    }

    public Optional<MyAccountDTO> updateTeacherSettings(MyAccountDTO myAccountDTO) {
        return teacherRepository.findById(myAccountDTO.getId()).map(teacher -> updateTeacherSettings(myAccountDTO, teacher));
    }

    public String deleteTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Teacher %d not found", teacherId)));
        teacher.getUser().setActivated(false);
        teacher.setDeactivatedUntil(null);
        teacher.setActive(false);
        userService.clearUserCaches(teacher.getUser());
        log.info("Der Fahrlehrer {} wurde deaktiviert",
            teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName());
        return teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void setTeachersActive() {
        teacherRepository.findAllByDeactivatedUntilIsNotNullAndDeactivatedUntilLessThanEqual(LocalDate.now())
            .forEach(teacher -> {
                teacher.setDeactivatedUntil(null);
                teacher.setActive(true);
            });
    }

    @Transactional(readOnly = true)
    public List<TeacherDTO> getAllTeachers(boolean activeOnly) {
        if (activeOnly)
            return teacherRepository.findAllByUserActivatedIsTrue().stream()
                .map(TeacherDTO::new).collect(Collectors.toList());

        return teacherRepository.findAll().stream().map(TeacherDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TeacherDTO> getTeacher(Long teacherId) {
        return teacherRepository.findById(teacherId).map(TeacherDTO::new);
    }

    @Transactional(readOnly = true)
    public boolean isTeacherOfStudent(String teacherLogin, Long studentId) {
        return studentRepository.findByTeacherUserLoginAndId(teacherLogin, studentId).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<TeacherDTO> getTeacherByLogin(String login) {
        return teacherRepository.findByUserLogin(login).map(TeacherDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<MyAccountDTO> getTeacherForStudent(String login) {
        return studentRepository.findByUserLogin(login).filter(student -> student.getTeacher() != null)
            .map(student -> {
                Teacher teacher = student.getTeacher();
                MyAccountDTO myAccountDTO = new MyAccountDTO();
                myAccountDTO.setPhoneNumber(teacher.getPhoneNumber());
                myAccountDTO.setId(teacher.getId());
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(teacher.getUser().getEmail());
                userDTO.setFirstName(teacher.getUser().getFirstName());
                userDTO.setLastName(teacher.getUser().getLastName());
                myAccountDTO.setUser(userDTO);
                return myAccountDTO;
            });
    }
}
