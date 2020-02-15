// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DrivingLessonComponentsPage, DrivingLessonDeleteDialog, DrivingLessonUpdatePage } from './driving-lesson.page-object';

const expect = chai.expect;

describe('DrivingLesson e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let drivingLessonComponentsPage: DrivingLessonComponentsPage;
  let drivingLessonUpdatePage: DrivingLessonUpdatePage;
  let drivingLessonDeleteDialog: DrivingLessonDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load DrivingLessons', async () => {
    await navBarPage.goToEntity('driving-lesson');
    drivingLessonComponentsPage = new DrivingLessonComponentsPage();
    await browser.wait(ec.visibilityOf(drivingLessonComponentsPage.title), 5000);
    expect(await drivingLessonComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.drivingLesson.home.title');
  });

  it('should load create DrivingLesson page', async () => {
    await drivingLessonComponentsPage.clickOnCreateButton();
    drivingLessonUpdatePage = new DrivingLessonUpdatePage();
    expect(await drivingLessonUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.drivingLesson.home.createOrEditLabel');
    await drivingLessonUpdatePage.cancel();
  });

  it('should create and save DrivingLessons', async () => {
    const nbButtonsBeforeCreate = await drivingLessonComponentsPage.countDeleteButtons();

    await drivingLessonComponentsPage.clickOnCreateButton();
    await promise.all([
      drivingLessonUpdatePage.categorySelectLastOption(),
      drivingLessonUpdatePage.lessonTypeSelectLastOption(),
      drivingLessonUpdatePage.driverSelectLastOption(),
      drivingLessonUpdatePage.missingSelectLastOption(),
      drivingLessonUpdatePage.timeSlotSelectLastOption()
    ]);
    await drivingLessonUpdatePage.save();
    expect(await drivingLessonUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await drivingLessonComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last DrivingLesson', async () => {
    const nbButtonsBeforeDelete = await drivingLessonComponentsPage.countDeleteButtons();
    await drivingLessonComponentsPage.clickOnLastDeleteButton();

    drivingLessonDeleteDialog = new DrivingLessonDeleteDialog();
    expect(await drivingLessonDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.drivingLesson.delete.question');
    await drivingLessonDeleteDialog.clickOnConfirmButton();

    expect(await drivingLessonComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
