import {by, element, ElementFinder} from 'protractor';
import {StudentManagementPage, TeacherManagementPage} from "./administration-page-objects";

/* eslint @typescript-eslint/no-use-before-define: 0 */
export class NavBarPage {
    entityMenu = element(by.id('entity-menu'));
    accountMenu = element(by.id('account-menu'));
    adminMenu: ElementFinder;
    signIn = element(by.id('login'));
    register = element(by.css('[routerLink="account/register"]'));
    signOut = element(by.id('logout'));
    passwordMenu = element(by.css('[routerLink="account/password"]'));
    settingsMenu = element(by.css('[routerLink="account/settings"]'));
    studentSettingsMenu = element(by.css('[routerLink="account/student-settings"]'));

    constructor(asAdmin?: Boolean) {
        if (asAdmin) {
            this.adminMenu = element(by.id('admin-menu'));
        }
    }

    async clickOnEntityMenu() {
        await this.entityMenu.click();
    }

    async clickOnAccountMenu() {
        await this.accountMenu.click();
    }

    async clickOnAdminMenu() {
        await this.adminMenu.click();
    }

    async clickOnSignIn() {
        await this.signIn.click();
    }

    async clickOnSignOut() {
        await this.signOut.click();
    }

    async clickOnPasswordMenu() {
        await this.passwordMenu.click();
    }

    async clickOnSettingsMenu() {
        await this.settingsMenu.click();
    }

    async clickOnStudentSettingsMenu() {
        await this.studentSettingsMenu.click();
    }

    async clickOnEntity(entityName: string) {
        await element(by.css('[routerLink="' + entityName + '"]')).click();
    }

    async clickOnAdmin(entityName: string) {
        await element(by.css('[routerLink="admin/' + entityName + '"]')).click();
    }

    async getSignInPage() {
        await this.clickOnAccountMenu();
        await this.clickOnSignIn();
        return new SignInPage();
    }

    async getPasswordPage() {
        await this.clickOnAccountMenu();
        await this.clickOnPasswordMenu();
        return new PasswordPage();
    }

    async getSettingsPage() {
        await this.clickOnAccountMenu();
        await this.clickOnSettingsMenu();
        return new SettingsPage();
    }

    async getStudentSettingsPage() {
        await this.clickOnAccountMenu();
        await this.clickOnStudentSettingsMenu();
        return new SettingsPage();
    }

    async getTeacherManagementPage() {
        await this.clickOnAdminMenu();
        await this.clickOnAdmin("teacher-management");
        return new TeacherManagementPage();
    }

    async getStudentManagementPage() {
        await this.clickOnAdminMenu();
        await this.clickOnAdmin("student-management");
        return new StudentManagementPage();
    }

    async goToEntity(entityName: string) {
        await this.clickOnEntityMenu();
        await this.clickOnEntity(entityName);
    }

    async autoSignOut() {
        await this.clickOnAccountMenu();
        await this.clickOnSignOut();
    }

}

export class SignInPage {
    username = element(by.id('username'));
    password = element(by.id('password'));
    loginButton = element(by.css('button[type=submit]'));

    async setUserName(username) {
        await this.username.sendKeys(username);
    }

    async setPassword(password) {
        await this.password.sendKeys(password);
    }

    async autoSignInUsing(username: string, password: string) {
        await this.setUserName(username);
        await this.setPassword(password);
        await this.login();
    }

    async login() {
        await this.loginButton.click();
    }
}

export class PasswordPage {
    currentPassword = element(by.id('currentPassword'));
    password = element(by.id('newPassword'));
    confirmPassword = element(by.id('confirmPassword'));
    saveButton = element(by.css('button[type=submit]'));
    title = element.all(by.css('h2')).first();

    async setCurrentPassword(password) {
        await this.currentPassword.sendKeys(password);
    }

    async setPassword(password) {
        await this.password.sendKeys(password);
    }

    async setConfirmPassword(confirmPassword) {
        await this.confirmPassword.sendKeys(confirmPassword);
    }

    async getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }

    async save() {
        await this.saveButton.click();
    }
}

export class SettingsPage {
    firstName = element(by.id('firstName'));
    lastName = element(by.id('lastName'));
    email = element(by.id('email'));
    saveButton = element(by.css('button[type=submit]'));
    title = element.all(by.css('h2')).first();
    wantedLessons = element(by.id("wantedLessons"));

    async setFirstName(firstName) {
        await this.firstName.sendKeys(firstName);
    }

    async setLastName(lastName) {
        await this.lastName.sendKeys(lastName);
    }

    async setEmail(email) {
        await this.email.sendKeys(email);
    }

    async getTitle() {
        return this.title.getAttribute('jhitranslate');
    }

    async autoSetWantedLessons(lessons: number) {
        await this.wantedLessons.clear();
        await this.wantedLessons.sendKeys(lessons);
        await this.save();
        return this.wantedLessons.getAttribute("value");
    }

    async save() {
        await this.saveButton.click();
    }
}
