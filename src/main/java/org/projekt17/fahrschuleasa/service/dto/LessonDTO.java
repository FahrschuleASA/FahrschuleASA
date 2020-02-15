package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Lesson;

import java.time.LocalDateTime;

public class LessonDTO {

    private Long id;

    private LocalDateTime begin;

    private LocalDateTime end;

    public LessonDTO() {}

    public LessonDTO(Lesson lesson) {
        this.id = lesson.getId();
        this.begin = lesson.getBegin();
        this.end = lesson.getEnd();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "LessonDTO{" +
            "id=" + id +
            ", begin=" + begin +
            ", end=" + end +
            '}';
    }
}
