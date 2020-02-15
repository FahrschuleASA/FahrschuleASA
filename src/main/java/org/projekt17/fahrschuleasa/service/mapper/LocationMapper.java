package org.projekt17.fahrschuleasa.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.service.dto.LocationDTO;

@Mapper(componentModel = "spring")
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationDTO toDto(Location location);

    Location toEntity(Location location);

    default Location fromId(Long id){
        if (id == null) {
            return null;
        }
        Location location = new Location();
        location.setId(id);
        return location;
    }
}
