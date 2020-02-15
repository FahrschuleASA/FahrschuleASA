// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { TeacherComponentsPage, TeacherDeleteDialog, TeacherUpdatePage } from './teacher.page-object';

const expect = chai.expect;

describe('Teacher e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let teacherComponentsPage: TeacherComponentsPage;
  let teacherUpdatePage: TeacherUpdatePage;
  let teacherDeleteDialog: TeacherDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Teachers', async () => {
    await navBarPage.goToEntity('teacher');
    teacherComponentsPage = new TeacherComponentsPage();
    await browser.wait(ec.visibilityOf(teacherComponentsPage.title), 5000);
    expect(await teacherComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.teacher.home.title');
  });

  it('should load create Teacher page', async () => {
    await teacherComponentsPage.clickOnCreateButton();
    teacherUpdatePage = new TeacherUpdatePage();
    expect(await teacherUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.teacher.home.createOrEditLabel');
    await teacherUpdatePage.cancel();
  });

  it('should create and save Teachers', async () => {
    const nbButtonsBeforeCreate = await teacherComponentsPage.countDeleteButtons();

    await teacherComponentsPage.clickOnCreateButton();
    await promise.all([]);
    const selectedChangedTimeSlots = teacherUpdatePage.getChangedTimeSlotsInput();
    if (await selectedChangedTimeSlots.isSelected()) {
      await teacherUpdatePage.getChangedTimeSlotsInput().click();
      expect(await teacherUpdatePage.getChangedTimeSlotsInput().isSelected(), 'Expected changedTimeSlots not to be selected').to.be.false;
    } else {
      await teacherUpdatePage.getChangedTimeSlotsInput().click();
      expect(await teacherUpdatePage.getChangedTimeSlotsInput().isSelected(), 'Expected changedTimeSlots to be selected').to.be.true;
    }
    await teacherUpdatePage.save();
    expect(await teacherUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await teacherComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Teacher', async () => {
    const nbButtonsBeforeDelete = await teacherComponentsPage.countDeleteButtons();
    await teacherComponentsPage.clickOnLastDeleteButton();

    teacherDeleteDialog = new TeacherDeleteDialog();
    expect(await teacherDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.teacher.delete.question');
    await teacherDeleteDialog.clickOnConfirmButton();

    expect(await teacherComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
