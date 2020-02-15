package org.projekt17.fahrschuleasa.web.rest;


import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.projekt17.fahrschuleasa.domain.User;
import org.projekt17.fahrschuleasa.security.AuthoritiesConstants;
import org.projekt17.fahrschuleasa.security.SecurityUtils;
import org.projekt17.fahrschuleasa.service.MailService;
import org.projekt17.fahrschuleasa.service.StudentService;
import org.projekt17.fahrschuleasa.service.UserService;
import org.projekt17.fahrschuleasa.service.dto.MyAccountDTO;
import org.projekt17.fahrschuleasa.service.dto.PasswordChangeDTO;
import org.projekt17.fahrschuleasa.service.dto.UserDTO;
import org.projekt17.fahrschuleasa.web.rest.errors.EmailNotFoundException;
import org.projekt17.fahrschuleasa.web.rest.errors.InvalidPasswordException;
import org.projekt17.fahrschuleasa.web.rest.vm.KeyAndPasswordVM;
import org.projekt17.fahrschuleasa.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final StudentService studentService;

    private final MailService mailService;

    public AccountResource(UserService userService, StudentService studentService, MailService mailService) {
        this.userService = userService;
        this.studentService = studentService;
        this.mailService = mailService;
    }

    @GetMapping("/activate/email")
    public void activateNewEmail(@RequestParam(value = "key") String key) {
        userService.activateNewMail(key);
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code GET /my-account} : returns an account
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated myAccountDTO,
     *         or with status {@code 404 (NotFound)}.
     */
    @GetMapping("/my-account")
    public ResponseEntity<MyAccountDTO> getMyAccount() {
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin().flatMap(userService::getMyAccountByLogin));
    }

    /**
     * {@code PUT /my-account} : updates the personal information of th currently logged in student account
     *
     * @param myAccountDTO containing the new information
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated myAccountDTO,
     *         or with status {@code 404 (NotFound)}.
     */
    @PutMapping("/my-account/student")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.STUDENT + "\")")
    public ResponseEntity<MyAccountDTO> updateMyAccountStudent(@Valid @RequestBody MyAccountDTO myAccountDTO) {
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> studentService.updateStudentMyAccountSettings(myAccountDTO, login)),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, "personalData", myAccountDTO.getId().toString()));
    }

    /**
     * {@code PUT /my-account} : updates the personal information of an account
     *
     * @param myAccountDTO containing the new information
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated myAccountDTO,
     *         or with status {@code 404 (NotFound)}.
     */
    @PutMapping("/my-account")
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.TEACHER + "\")")
    public ResponseEntity<MyAccountDTO> updateMyAccount(@Valid @RequestBody MyAccountDTO myAccountDTO) {
        return ResponseUtil.wrapOrNotFound(SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userService.updateMyAccount(myAccountDTO, login)),
            HeaderUtil.createEntityUpdateAlert(applicationName, true, "personalData", myAccountDTO.getId().toString()));
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     * @throws EmailNotFoundException {@code 400 (Bad Request)} if the email address is not registered.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
       mailService.sendPasswordResetMail(
           userService.requestPasswordReset(mail)
               .orElseThrow(EmailNotFoundException::new)
       );
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    /**
     * @param password, the password to check
     *
     * @return a boolean whether the password has a correct length or not
     */
    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
