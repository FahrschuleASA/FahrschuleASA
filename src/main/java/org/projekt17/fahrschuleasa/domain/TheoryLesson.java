package org.projekt17.fahrschuleasa.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A TheoryLesson.
 */
@Entity
@Table(name = "theory_lesson")
public class TheoryLesson extends Lesson {

    public TheoryLesson() {}

    @Column(name = "subject")
    private String subject;

    @ManyToOne
    @JsonIgnoreProperties("theoryLessons")
    private Teacher teacher;

    @ManyToMany(mappedBy = "theoryLessons")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Student> students = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public String getSubject() {
        return subject;
    }

    public TheoryLesson subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public TheoryLesson teacher(Teacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public TheoryLesson students(Set<Student> students) {
        this.students = students;
        return this;
    }

    public TheoryLesson addStudent(Student student) {
        this.students.add(student);
        student.getTheoryLessons().add(this);
        return this;
    }

    public TheoryLesson removeStudent(Student student) {
        this.students.remove(student);
        student.getTheoryLessons().remove(this);
        return this;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TheoryLesson{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            "}";
    }
}
