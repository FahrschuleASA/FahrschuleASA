package org.projekt17.fahrschuleasa.service.dto;

import org.projekt17.fahrschuleasa.domain.Location;
import org.projekt17.fahrschuleasa.domain.MyAccount;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MyAccountDTO {

    private Long id;

    private Boolean active;

    private LocalDate birthdate;

    private Integer deactivatedDaysLeft;

    private Location address;

    private String phoneNumber;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254)
    private String newEmail;

    @Valid
    private UserDTO user;

    public MyAccountDTO(){}

    public MyAccountDTO(MyAccount myAccount){
        id = myAccount.getId();
        active = myAccount.isActive();
        birthdate = myAccount.getBirthdate();
        if (myAccount.getDeactivatedUntil() != null)
            deactivatedDaysLeft = (int) LocalDate.now().until(myAccount.getDeactivatedUntil(), ChronoUnit.DAYS);
        address = myAccount.getAddress();
        phoneNumber = myAccount.getPhoneNumber();
        newEmail = myAccount.getNewEmail();

        user = new UserDTO(myAccount.getUser());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getDeactivatedDaysLeft() {
        return deactivatedDaysLeft;
    }

    public void setDeactivatedDaysLeft(Integer deactivatedDaysLeft) {
        this.deactivatedDaysLeft = deactivatedDaysLeft;
    }

    public Location getAddress() {
        return address;
    }

    public void setAddress(Location address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "MyAccountDTO{" +
            "id=" + id +
            ", active=" + active +
            ", birthdate=" + birthdate +
            ", address=" + address +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", user=" + user +
            '}';
    }
}
