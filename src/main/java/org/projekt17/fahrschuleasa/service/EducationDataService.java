package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.service.dto.EducationDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class EducationDataService {

    private final Logger log = LoggerFactory.getLogger(EducationDataService.class);

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    private final StudentService studentService;

    private final UserService userService;

    public EducationDataService(StudentRepository studentRepository, TeacherRepository teacherRepository,
                                StudentService studentService, UserService userService) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.studentService = studentService;
        this.userService = userService;
    }

    public Optional<EducationDataDTO> updateEducationData(EducationDataDTO educationDataDTO) {
        return studentRepository.findById(educationDataDTO.getStudentId()).map(student -> {
            TeachingDiagram teachingDiagram = student.getTeachingDiagram();
            teachingDiagram.setBasic(educationDataDTO.getBasic());
            teachingDiagram.setAdvanced(educationDataDTO.getAdvanced());
            teachingDiagram.setPerformance(educationDataDTO.getPerformance());
            teachingDiagram.setIndependence(educationDataDTO.getIndependence());
            teachingDiagram.setOverland(educationDataDTO.getOverland());
            teachingDiagram.setAutobahn(educationDataDTO.getAutobahn());
            teachingDiagram.setNight(educationDataDTO.getNight());

            studentService.updateDrivingCategory(student, educationDataDTO.getDrivingCategory());
            student.setMissionAccomplished(educationDataDTO.getMissionAccomplished());
            if (educationDataDTO.getMissionAccomplished()) {
                student.setActive(false);
                student.setDeactivatedUntil(null);
                student.getUser().setActivated(false);
                userService.clearUserCaches(student.getUser());
            }
            student.setAllowedLessons(educationDataDTO.getAllowedLessons());
            if (student.getWantedLessons() > student.getAllowedLessons())
                student.setWantedLessons(student.getAllowedLessons());
            student.setTeacher(teacherRepository.findById(educationDataDTO.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("teacher %d not found",
                    educationDataDTO.getTeacherId()))));

            log.info("Die ausbildungsbezogenen Daten des Fahrschülers {} wurden aktualisiert: {}",
                student.getUser().getFirstName() + " " + student.getUser().getLastName(), student.toString());

            return new EducationDataDTO(student);
        });
    }

    @Transactional(readOnly = true)
    public Optional<EducationDataDTO> getEducationDataForStudent(String login) {
        return studentRepository.findByUserLogin(login).map(EducationDataDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<EducationDataDTO> getEducationDataByStudentId(Long id) {
        return studentRepository.findById(id).map(EducationDataDTO::new);
    }
}
