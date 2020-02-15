package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Administrator;

public class AdministratorDTO extends MyAccountDTO {

    public AdministratorDTO(){}

    public AdministratorDTO(Administrator administrator){
        super(administrator);
    }
}
