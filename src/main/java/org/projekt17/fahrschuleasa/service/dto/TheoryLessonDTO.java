package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.TheoryLesson;

import java.util.Set;
import java.util.stream.Collectors;

public class TheoryLessonDTO extends LessonDTO {

    private String subject;

    private Set<SmallStudentDTO> students;

    private Long teacherId;

    public TheoryLessonDTO() {}

    public TheoryLessonDTO(TheoryLesson theoryLesson) {
        super(theoryLesson);
        subject = theoryLesson.getSubject();
        if (theoryLesson.getTeacher() != null)
            teacherId = theoryLesson.getTeacher().getId();
        students = theoryLesson.getStudents().stream().map(SmallStudentDTO::new).collect(Collectors.toSet());
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Set<SmallStudentDTO> getStudents() {
        return students;
    }

    public void setStudents(Set<SmallStudentDTO> students) {
        this.students = students;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "TheoryLessonDTO{" +
            "id=" + super.getId() +
            ", begin=" + super.getBegin() +
            ", end=" + super.getEnd() +
            ", subject='" + subject + '\'' +
            ", teacherId=" + teacherId +
            '}';
    }
}
