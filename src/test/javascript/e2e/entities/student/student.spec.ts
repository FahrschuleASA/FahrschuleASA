// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { StudentComponentsPage, StudentDeleteDialog, StudentUpdatePage } from './student.page-object';

const expect = chai.expect;

describe('Student e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let studentComponentsPage: StudentComponentsPage;
  let studentUpdatePage: StudentUpdatePage;
  let studentDeleteDialog: StudentDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Students', async () => {
    await navBarPage.goToEntity('student');
    studentComponentsPage = new StudentComponentsPage();
    await browser.wait(ec.visibilityOf(studentComponentsPage.title), 5000);
    expect(await studentComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.student.home.title');
  });

  it('should load create Student page', async () => {
    await studentComponentsPage.clickOnCreateButton();
    studentUpdatePage = new StudentUpdatePage();
    expect(await studentUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.student.home.createOrEditLabel');
    await studentUpdatePage.cancel();
  });

  it('should create and save Students', async () => {
    const nbButtonsBeforeCreate = await studentComponentsPage.countDeleteButtons();

    await studentComponentsPage.clickOnCreateButton();
    await promise.all([
      studentUpdatePage.categorySelectLastOption(),
      studentUpdatePage.setWantedLessonsInput('5'),
      studentUpdatePage.teachingDiagramSelectLastOption(),
      // studentUpdatePage.theoryLessonsSelectLastOption(),
      // studentUpdatePage.optionalDrivingLessonsSelectLastOption(),
      studentUpdatePage.teacherSelectLastOption()
    ]);
    const selectedReadyForTheory = studentUpdatePage.getReadyForTheoryInput();
    if (await selectedReadyForTheory.isSelected()) {
      await studentUpdatePage.getReadyForTheoryInput().click();
      expect(await studentUpdatePage.getReadyForTheoryInput().isSelected(), 'Expected readyForTheory not to be selected').to.be.false;
    } else {
      await studentUpdatePage.getReadyForTheoryInput().click();
      expect(await studentUpdatePage.getReadyForTheoryInput().isSelected(), 'Expected readyForTheory to be selected').to.be.true;
    }
    expect(await studentUpdatePage.getWantedLessonsInput()).to.eq('5', 'Expected wantedLessons value to be equals to 5');
    const selectedChangedPreferences = studentUpdatePage.getChangedPreferencesInput();
    if (await selectedChangedPreferences.isSelected()) {
      await studentUpdatePage.getChangedPreferencesInput().click();
      expect(await studentUpdatePage.getChangedPreferencesInput().isSelected(), 'Expected changedPreferences not to be selected').to.be
        .false;
    } else {
      await studentUpdatePage.getChangedPreferencesInput().click();
      expect(await studentUpdatePage.getChangedPreferencesInput().isSelected(), 'Expected changedPreferences to be selected').to.be.true;
    }
    await studentUpdatePage.save();
    expect(await studentUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await studentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Student', async () => {
    const nbButtonsBeforeDelete = await studentComponentsPage.countDeleteButtons();
    await studentComponentsPage.clickOnLastDeleteButton();

    studentDeleteDialog = new StudentDeleteDialog();
    expect(await studentDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.student.delete.question');
    await studentDeleteDialog.clickOnConfirmButton();

    expect(await studentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
