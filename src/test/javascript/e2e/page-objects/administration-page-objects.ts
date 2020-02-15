import {by, element} from "protractor";

/* eslint @typescript-eslint/no-use-before-define: 0 */
export class TeacherManagementPage {

    title = element(by.id("student-management-page-heading"));
    newTeacherButton = element.all(by.tagName("button")).first();

    async getTitle() {
        return await this.title.getAttribute("jhiTranslate");
    }

    async clickNewTeacherButton() {
        await this.newTeacherButton.click();
        return new NewTeacherPage();
    }

}

export class StudentManagementPage {

    title = element(by.id("student-management-page-heading"));
    newStudentButton = element.all(by.tagName("button")).first();

    async getTitle() {
        return await this.title.getAttribute("jhiTranslate");
    }

    async clickNewStudentButton() {
        await this.newStudentButton.click();
        return new NewStudentPage();
    }

}

export class NewTeacherPage {

    // Input fields
    firstName = element(by.id("firstName"));
    lastName = element(by.id("lastName"));
    birthDate = element(by.id("birthDate"));
    email = element(by.id("email"));
    phoneNumber = element(by.id("phoneNumber"));
    street = element(by.id("street"));
    houseNumber = element(by.id("houseNumber"));
    postal = element(by.id("postal"));
    town = element(by.id("town"));
    country = element(by.id("country"));
    schoolOwner = element(by.id("schoolOwner"));

    // Buttons
    saveButton = element(by.css('[type="submit"]'));

    async typeFirstName(value) {
        this.firstName.sendKeys(value);
    }

    async typeLastName(value) {
        this.lastName.sendKeys(value);
    }

    async typeBirthDate(value) {
        this.birthDate.sendKeys(value);
    }

    async typeEmail(value) {
        this.email.sendKeys(value);
    }

    async typePhoneNumber(value) {
        this.phoneNumber.sendKeys(value);
    }

    async typeStreet(value) {
        this.street.sendKeys(value);
    }

    async typeHouseNumber(value) {
        this.houseNumber.sendKeys(value);
    }

    async typePostal(value) {
        this.postal.sendKeys(value);
    }

    async typeTown(value) {
        this.town.sendKeys(value);
    }

    async typeCountry(value) {
        this.country.sendKeys(value);
    }

    async typeSchoolOwner(value) {
        this.schoolOwner.sendKeys(value);
    }

    async clickSave() {
        await this.saveButton.click();
    }

}

export class NewStudentPage {

    // Input fields
    firstName = element(by.id("firstName"));
    lastName = element(by.id("lastName"));
    birthDate = element(by.id("birthDate"));
    email = element(by.id("email"));
    phoneNumber = element(by.id("phoneNumber"));
    street = element(by.id("street"));
    houseNumber = element(by.id("houseNumber"));
    postal = element(by.id("postal"));
    town = element(by.id("town"));
    country = element(by.id("country"));
    teacher = element(by.id("teacher"));
    category = element(by.id("category"));

    // Buttons
    saveButton = element(by.css('[type="submit"]'));

    async typeFirstName(value) {
        this.firstName.sendKeys(value);
    }

    async typeLastName(value) {
        this.lastName.sendKeys(value);
    }

    async typeBirthDate(value) {
        this.birthDate.sendKeys(value);
    }

    async typeEmail(value) {
        this.email.sendKeys(value);
    }

    async typePhoneNumber(value) {
        this.phoneNumber.sendKeys(value);
    }

    async typeStreet(value) {
        this.street.sendKeys(value);
    }

    async typeHouseNumber(value) {
        this.houseNumber.sendKeys(value);
    }

    async typePostal(value) {
        this.postal.sendKeys(value);
    }

    async typeTown(value) {
        this.town.sendKeys(value);
    }

    async typeCountry(value) {
        this.country.sendKeys(value);
    }

    async typeTeacher(value) {
        this.teacher.sendKeys(value);
    }

    async typeCategory(value) {
        this.category.sendKeys(value);
    }

    async clickSave() {
        await this.saveButton.click();
    }

}
