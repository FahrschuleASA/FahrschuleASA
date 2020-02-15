package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Student;

public class SmallStudentDTO {

    private Long studentId;

    private String firstname;

    private String lastname;

    public SmallStudentDTO(){}

    public SmallStudentDTO(Student student){
        this.studentId = student.getId();
        if (student.getUser() != null) {
            this.firstname = student.getUser().getFirstName();
            this.lastname = student.getUser().getLastName();
        }
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "SmallStudentDTO{" +
            "studentId=" + studentId +
            ", firstname='" + firstname + '\'' +
            ", lastname='" + lastname + '\'' +
            '}';
    }
}
