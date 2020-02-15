package org.projekt17.fahrschuleasa.service;

import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.TheoryLesson;
import org.projekt17.fahrschuleasa.repository.StudentRepository;
import org.projekt17.fahrschuleasa.repository.TeacherRepository;
import org.projekt17.fahrschuleasa.repository.TheoryLessonRepository;
import org.projekt17.fahrschuleasa.service.dto.TheoryLessonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class TheoryLessonService {

    private final Logger log = LoggerFactory.getLogger(TheoryLessonService.class);

    private final TheoryLessonRepository theoryLessonRepository;

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    public TheoryLessonService(TheoryLessonRepository theoryLessonRepository, StudentRepository studentRepository
            , TeacherRepository teacherRepository) {
        this.theoryLessonRepository = theoryLessonRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public Optional<TheoryLessonDTO> createTheoryLesson(TheoryLessonDTO theoryLessonDTO, String login) {
        TheoryLesson newTheoryLesson = new TheoryLesson();
        return teacherRepository.findByUserLogin(login).map(
            teacher -> {
                newTheoryLesson.setBegin(theoryLessonDTO.getBegin());
                newTheoryLesson.setEnd(theoryLessonDTO.getEnd());
                newTheoryLesson.setSubject(theoryLessonDTO.getSubject());
                teacher.addTheoryLesson(newTheoryLesson);
                log.info("Es wurde eine neue Theoriestunde angelegt: {}", newTheoryLesson);
                return new TheoryLessonDTO(theoryLessonRepository.save(newTheoryLesson));
            }
        );
    }
    public Optional<TheoryLessonDTO> createTheoryLesson(TheoryLessonDTO theoryLessonDTO) {
        TheoryLesson newTheoryLesson = new TheoryLesson();
        return teacherRepository.findById(theoryLessonDTO.getTeacherId()).map(
            teacher -> {
                newTheoryLesson.setBegin(theoryLessonDTO.getBegin());
                newTheoryLesson.setEnd(theoryLessonDTO.getEnd());
                newTheoryLesson.setSubject(theoryLessonDTO.getSubject());
                log.info("Es wurde eine neue Theoriestunde angelegt: {}", newTheoryLesson);
                teacher.addTheoryLesson(newTheoryLesson);
                return new TheoryLessonDTO(theoryLessonRepository.save(newTheoryLesson));
            }
        );
    }

    public Optional<TheoryLessonDTO> changeTheoryLessonSubject(Long lessonId, String subject) {
        return theoryLessonRepository.findById(lessonId).map(theoryLesson -> {
            theoryLesson.setSubject(subject);
            log.info("Das Thema einer Theoriestunde wurde geändert: {}", theoryLesson);
            return new TheoryLessonDTO(theoryLesson);
        });
    }

    public Optional<TheoryLessonDTO> addStudentToTheoryLesson(Long lessonId, Long studentId) {
        return theoryLessonRepository.findById(lessonId).map(theoryLesson -> {
           Student student = studentRepository.findById(studentId)
               .orElseThrow(() -> new IllegalArgumentException(String.format("Student %d not found", studentId)));
           theoryLesson.addStudent(student);
           log.info("Der Fahrschüler {} wurde der Theoriestunde {} hinzugefügt",
               student, theoryLesson);
           return new TheoryLessonDTO(theoryLesson);
        });
    }

    public Optional<TheoryLessonDTO> removeStudentFromTheoryLessson(Long lessonId, Long studentId) {
        return theoryLessonRepository.findById(lessonId).map(theoryLesson -> {
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Student %d not found", studentId)));
            theoryLesson.removeStudent(student);
            log.info("Der Fahrschüler {} wurde von der Theoriestunde {} entfernt",
                student, theoryLesson);
            return new TheoryLessonDTO(theoryLesson);
        });
    }

    @Transactional(readOnly = true)
    public List<TheoryLessonDTO> getAllTheoryLessons() {
        return theoryLessonRepository.findAll().stream().map(TheoryLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TheoryLessonDTO> getAllTheoryLessonsByTeacherId(Long id) {
        return theoryLessonRepository.findAllByTeacherId(id).stream()
            .map(TheoryLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TheoryLessonDTO> getAllTheoryLessonsByTeacherLogin(String login) {
        return theoryLessonRepository.findAllByTeacherUserLogin(login).stream()
            .map(TheoryLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TheoryLessonDTO> getAllFutureTheoryLessonsByTeacherId(Long id) {
        return theoryLessonRepository.findAllByTeacherIdAndBeginAfter(id, LocalDateTime.now()).stream()
            .map(TheoryLessonDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<List<TheoryLessonDTO>> getAllTheoryLessonsByStudentLogin(String login) {
        return studentRepository.findByUserLogin(login).map(student -> student.getTheoryLessons().stream()
            .map(TheoryLessonDTO::new).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public Optional<List<TheoryLessonDTO>> getAllTheoryLessonsByStudentId(Long id) {
        return studentRepository.findById(id).map(student -> student.getTheoryLessons().stream()
            .map(TheoryLessonDTO::new).collect(Collectors.toList()));
    }

    public void deleteTheoryLesson(Long id) {
        theoryLessonRepository.deleteById(id);
        log.info("Es wurde eine Theoriestunde gelöscht");
    }

    @Transactional(readOnly = true)
    public Optional<TheoryLessonDTO> getTheoryLesson(Long id){
        return theoryLessonRepository.findById(id).map(TheoryLessonDTO::new);
    }
}
