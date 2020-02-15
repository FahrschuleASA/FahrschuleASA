package org.projekt17.fahrschuleasa.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A MyAccount.
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@MappedSuperclass
public class MyAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "deactivated_until")
    private LocalDate deactivatedUntil;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("myAccounts")
    private Location address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254)
    private String newEmail;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isActive() {
        return active;
    }

    public MyAccount active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getDeactivatedUntil() {
        return deactivatedUntil;
    }

    public MyAccount deactivatedUntil(LocalDate deactivatedUntil) {
        this.deactivatedUntil = deactivatedUntil;
        return this;
    }

    public void setDeactivatedUntil(LocalDate deactivatedUntil) {
        this.deactivatedUntil = deactivatedUntil;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public MyAccount birthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Location getAddress() {
        return address;
    }

    public MyAccount address(Location address) {
        this.address = address;
        return this;
    }

    public void setAddress(Location address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public MyAccount phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public MyAccount newEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public User getUser() {
        return user;
    }

    public MyAccount user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyAccount)) {
            return false;
        }
        return id != null && id.equals(((MyAccount) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "MyAccount{" +
            "id=" + id +
            ", active=" + active +
            ", deactivatedUntil=" + deactivatedUntil +
            ", date of birth=" + birthdate +
            ", address=" + address +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", newEmail='" + newEmail + '\'' +
            ", user=" + user +
            '}';
    }
}
