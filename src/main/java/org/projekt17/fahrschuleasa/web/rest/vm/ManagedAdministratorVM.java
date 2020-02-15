package org.projekt17.fahrschuleasa.web.rest.vm;

import org.projekt17.fahrschuleasa.service.dto.AdministratorDTO;

import javax.validation.constraints.Size;

public class ManagedAdministratorVM extends AdministratorDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedAdministratorVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + "} ";
    }
}
