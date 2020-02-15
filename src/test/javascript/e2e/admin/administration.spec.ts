import {browser, by, element, ExpectedConditions as ec} from 'protractor';

import {NavBarPage, SignInPage} from '../page-objects/jhi-page-objects';
import {
    NewStudentPage,
    NewTeacherPage,
    StudentManagementPage,
    TeacherManagementPage
} from "../page-objects/administration-page-objects";

const expect = chai.expect;

describe('administration', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let teacherManagementPage: TeacherManagementPage;
    let studentManagementPage: StudentManagementPage;
    let newTeacherPage: NewTeacherPage;
    let newStudent: NewStudentPage;

    before(async () => {
        signInPage = new SignInPage();
        navBarPage = new NavBarPage(true);

        await browser.get('/');
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.adminMenu), 5000);
    });

    it("should create new teacher", async () => {
        teacherManagementPage = await navBarPage.getTeacherManagementPage();

        const expect1 = "teacherManagement.home.title";
        const value1 = await teacherManagementPage.getTitle();
        expect(value1).to.eq(expect1);

        newTeacherPage = await teacherManagementPage.clickNewTeacherButton();
        await newTeacherPage.typeFirstName("Neuer");
        await newTeacherPage.typeLastName("Komischernachname");
        await newTeacherPage.typeBirthDate("01.01.1990");
        await newTeacherPage.typeEmail("neuer.lehrer@fahrschule.de");
        await newTeacherPage.typePhoneNumber("012345678901");
        await newTeacherPage.typeStreet("Musterstraße");
        await newTeacherPage.typeHouseNumber("69");
        await newTeacherPage.typePostal("42069");
        await newTeacherPage.typeTown("Musterstadt");
        await newTeacherPage.typeCountry("Deutschland");

        await newTeacherPage.clickSave();

        await browser.wait(ec.visibilityOf(element(by.tagName('jhi-teacher-mgmt'))), 5000);
        const newTeacherLastNameElement = await element(by.cssContainingText("td", "Komischernachname"));
        expect(newTeacherLastNameElement).to.not.be.null;
        expect(newTeacherLastNameElement).to.not.be.undefined;

    });

    it("should create new student", async () => {
        studentManagementPage = await navBarPage.getStudentManagementPage();

        const expect1 = "teacherManagement.home.title";
        const value1 = await teacherManagementPage.getTitle();
        expect(value1).to.eq(expect1);

        newStudent = await studentManagementPage.clickNewStudentButton();
        await newStudent.typeFirstName("Neuer");
        await newStudent.typeLastName("Komischernachname");
        await newStudent.typeBirthDate("01.01.1990");
        await newStudent.typeEmail("neuer.lehrer@fahrschule.de");
        await newStudent.typePhoneNumber("012345678901");
        await newStudent.typeStreet("Musterstraße");
        await newStudent.typeHouseNumber("69");
        await newStudent.typePostal("42069");
        await newStudent.typeTown("Musterstadt");
        await newStudent.typeCountry("Deutschland");
        await newStudent.typeTeacher(1);
        await newStudent.typeCategory('BE');

        await newStudent.clickSave();

        await browser.wait(ec.visibilityOf(element(by.tagName('jhi-teacher-mgmt'))), 5000);
        const newTeacherLastNameElement = await element(by.cssContainingText("td", "Komischernachname"));
        expect(newTeacherLastNameElement).to.not.be.null;
        expect(newTeacherLastNameElement).to.not.be.undefined;

    });

    it("should not create new student; incorrect birthdate", async () => {
        studentManagementPage = await navBarPage.getStudentManagementPage();

        const expect1 = "teacherManagement.home.title";
        const value1 = await teacherManagementPage.getTitle();
        expect(value1).to.eq(expect1);

        newStudent = await studentManagementPage.clickNewStudentButton();
        await newStudent.typeFirstName("Neuer");
        await newStudent.typeLastName("Komischernachname");
        await newStudent.typeBirthDate("01.012.1990");
        await newStudent.typeEmail("neuer.lehrer@fahrschule.de");
        await newStudent.typePhoneNumber("012345678901");
        await newStudent.typeStreet("Musterstraße");
        await newStudent.typeHouseNumber("69");
        await newStudent.typePostal("66111");
        await newStudent.typeTown("Musterstadt");
        await newStudent.typeCountry("Deutschland");
        await newStudent.typeTeacher(1);
        await newStudent.typeCategory('BE');

        await newStudent.clickSave();

        // EXPECT error

    });

    it("should not create new student; incorrect postal", async () => {
        studentManagementPage = await navBarPage.getStudentManagementPage();

        const expect1 = "teacherManagement.home.title";
        const value1 = await teacherManagementPage.getTitle();
        expect(value1).to.eq(expect1);

        newStudent = await studentManagementPage.clickNewStudentButton();
        await newStudent.typeFirstName("Neuer");
        await newStudent.typeLastName("Komischernachname");
        await newStudent.typeBirthDate("01.01.1990");
        await newStudent.typeEmail("neuer.lehrer@fahrschule.de");
        await newStudent.typePhoneNumber("012345678901");
        await newStudent.typeStreet("Musterstraße");
        await newStudent.typeHouseNumber("69");
        await newStudent.typePostal("66111");
        await newStudent.typeTown("Musterstadt");
        await newStudent.typeCountry("Deutschland");
        await newStudent.typeTeacher(1);
        await newStudent.typeCategory('BE');

        await newStudent.clickSave();

        // EXPECT error

    });

    it("should not create new student; incorrect category", async () => {
        studentManagementPage = await navBarPage.getStudentManagementPage();

        const expect1 = "teacherManagement.home.title";
        const value1 = await teacherManagementPage.getTitle();
        expect(value1).to.eq(expect1);

        newStudent = await studentManagementPage.clickNewStudentButton();
        await newStudent.typeFirstName("Neuer");
        await newStudent.typeLastName("Komischernachname");
        await newStudent.typeBirthDate("01.01.1990");
        await newStudent.typeEmail("neuer.lehrer@fahrschule.de");
        await newStudent.typePhoneNumber("012345678901");
        await newStudent.typeStreet("Musterstraße");
        await newStudent.typeHouseNumber("69");
        await newStudent.typePostal("66111");
        await newStudent.typeTown("Musterstadt");
        await newStudent.typeCountry("Deutschland");
        await newStudent.typeTeacher(1);
        await newStudent.typeCategory('Kaiserslautern');

        await newStudent.clickSave();

        // EXPECT error

    });

    it("should not create new student; incorrect mail", async () => {
        studentManagementPage = await navBarPage.getStudentManagementPage();

        const expect1 = "teacherManagement.home.title";
        const value1 = await teacherManagementPage.getTitle();
        expect(value1).to.eq(expect1);

        newStudent = await studentManagementPage.clickNewStudentButton();
        await newStudent.typeFirstName("Neuer");
        await newStudent.typeLastName("Komischernachname");
        await newStudent.typeBirthDate("01.01.1990");
        await newStudent.typeEmail("neuer.lehrer");
        await newStudent.typePhoneNumber("012345678901");
        await newStudent.typeStreet("Musterstraße");
        await newStudent.typeHouseNumber("69");
        await newStudent.typePostal("66111");
        await newStudent.typeTown("Musterstadt");
        await newStudent.typeCountry("Deutschland");
        await newStudent.typeTeacher(1);
        await newStudent.typeCategory('Kaiserslautern');

        await newStudent.clickSave();

        // EXPECT error

    });

});
