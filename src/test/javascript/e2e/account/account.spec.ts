import {browser, by, element, ExpectedConditions as ec} from 'protractor';

import {NavBarPage, PasswordPage, SettingsPage, SignInPage} from '../page-objects/jhi-page-objects';

const expect = chai.expect;

describe('account', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let passwordPage: PasswordPage;
    let settingsPage: SettingsPage;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage(true);
        signInPage = new SignInPage();
    });

    it("should redirect to /login if not logged in", async () => {
        await browser.get("/");
        const expect1 = "Login";
        const value1 = await element(by.tagName("h1")).getText();
        expect(value1).to.eq(expect1);
    });

    it('should fail to login with bad password', async () => {
        await browser.get('/');
        await signInPage.autoSignInUsing('admin', 'foo');

        const expect1 = 'login.messages.error.authentication';
        const value1 = await element(by.css('.alert-danger')).getAttribute('jhiTranslate');
        expect(value1).to.eq(expect1);
    });

    it('should login successfully with admin account', async () => {
        await browser.get('/');

        await signInPage.autoSignInUsing('admin', 'admin');

        await browser.wait(ec.visibilityOf(element(by.tagName('nav'))));
        const value1 = await element(by.tagName("jhi-home")).isPresent();
        expect(value1).to.eq(true);
    });

    it('should be able to update settings', async () => {
        settingsPage = await navBarPage.getSettingsPage();

        const expect1 = 'settings.title';
        const value1 = await settingsPage.getTitle();
        expect(value1).to.eq(expect1);

        await settingsPage.save();

        const expect2 = 'settings.messages.success';
        const alert = element(by.css('.alert-success'));
        const value2 = await alert.getAttribute('jhiTranslate');
        expect(value2).to.eq(expect2);
    });

    it("should be able to log out succesfully", async () => {
        await navBarPage.autoSignOut();
        const expect1 = "Login";
        const value1 = await element(by.tagName("h1")).getText();
        expect(value1).to.eq(expect1);

        // Should redirect to login again
        await browser.get("/admin/driving-school-configuration");
        const expect2 = "Login";
        const value2 = await element(by.tagName("h1")).getText();
        expect(value2).to.eq(expect2);
    });

    it("should login as student", async () => {
        await browser.get("/");
        await signInPage.autoSignInUsing('student1', 'student1');

        await browser.wait(ec.visibilityOf(element(by.tagName('nav'))));
        const value1 = await element(by.tagName("jhi-home")).isPresent();
        expect(value1).to.eq(true);

        const value2 = await element(by.css("[routerLink=\"account/teaching-data\"]")).isPresent();
        const expect2 = true;
        expect(value2).to.eq(expect2);
    });

    it("max lessons should never exceed 2", async () => {
        navBarPage = new NavBarPage(false);
        settingsPage = await navBarPage.getStudentSettingsPage();
        const expect1 = 'settings.title';
        const value1 = await settingsPage.getTitle();
        expect(value1).to.eq(expect1);

        const expect2 = "2";
        const value2 = await settingsPage.autoSetWantedLessons(4);
        expect(value2).to.eq(expect2);
    });

    it('should fail to update password when using incorrect current password', async () => {
        passwordPage = await navBarPage.getPasswordPage();

        expect(await passwordPage.getTitle()).to.eq('password.title');

        await passwordPage.setCurrentPassword('wrong_current_password');
        await passwordPage.setPassword('new_password');
        await passwordPage.setConfirmPassword('new_password');
        await passwordPage.save();

        const expect2 = 'password.messages.error';
        const alert = element(by.css('.alert-danger'));
        const value2 = await alert.getAttribute('jhiTranslate');
        expect(value2).to.eq(expect2);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
