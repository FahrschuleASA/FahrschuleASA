package org.projekt17.fahrschuleasa.service.mapper;

import org.projekt17.fahrschuleasa.domain.Student;
import org.projekt17.fahrschuleasa.domain.TeachingDiagram;
import org.projekt17.fahrschuleasa.service.dto.TeachingDiagramDTO;
import org.springframework.stereotype.Service;

@Service
public class TeachingDiagramMapper {

    public TeachingDiagramDTO toDto(Student student) {
        return new TeachingDiagramDTO(student);
    }

    public TeachingDiagram toEntity(TeachingDiagramDTO teachingDiagramDTO) {
        if (teachingDiagramDTO == null)
            return null;

        TeachingDiagram teachingDiagram = new TeachingDiagram();

        teachingDiagram.setAdvanced(teachingDiagramDTO.getAdvanced());
        teachingDiagram.setAutobahn(teachingDiagramDTO.getAutobahn());
        teachingDiagram.setBasic(teachingDiagramDTO.getBasic());
        teachingDiagram.setId(teachingDiagramDTO.getId());
        teachingDiagram.setIndependence(teachingDiagramDTO.getIndependence());
        teachingDiagram.setNight(teachingDiagramDTO.getNight());
        teachingDiagram.setOverland(teachingDiagramDTO.getOverland());
        teachingDiagram.setPerformance(teachingDiagramDTO.getPerformance());

        return teachingDiagram;
    }

    public TeachingDiagram fromId(Long id){
        if (id == null) {
            return null;
        }
        TeachingDiagram teachingDiagram = new TeachingDiagram();
        teachingDiagram.setId(id);
        return teachingDiagram;
    }
}
