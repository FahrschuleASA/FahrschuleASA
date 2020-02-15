// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { TheoryLessonComponentsPage, TheoryLessonDeleteDialog, TheoryLessonUpdatePage } from './theory-lesson.page-object';

const expect = chai.expect;

describe('TheoryLesson e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let theoryLessonComponentsPage: TheoryLessonComponentsPage;
  let theoryLessonUpdatePage: TheoryLessonUpdatePage;
  let theoryLessonDeleteDialog: TheoryLessonDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load TheoryLessons', async () => {
    await navBarPage.goToEntity('theory-lesson');
    theoryLessonComponentsPage = new TheoryLessonComponentsPage();
    await browser.wait(ec.visibilityOf(theoryLessonComponentsPage.title), 5000);
    expect(await theoryLessonComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.theoryLesson.home.title');
  });

  it('should load create TheoryLesson page', async () => {
    await theoryLessonComponentsPage.clickOnCreateButton();
    theoryLessonUpdatePage = new TheoryLessonUpdatePage();
    expect(await theoryLessonUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.theoryLesson.home.createOrEditLabel');
    await theoryLessonUpdatePage.cancel();
  });

  it('should create and save TheoryLessons', async () => {
    const nbButtonsBeforeCreate = await theoryLessonComponentsPage.countDeleteButtons();

    await theoryLessonComponentsPage.clickOnCreateButton();
    await promise.all([theoryLessonUpdatePage.setSubjectInput('subject'), theoryLessonUpdatePage.teacherSelectLastOption()]);
    expect(await theoryLessonUpdatePage.getSubjectInput()).to.eq('subject', 'Expected Subject value to be equals to subject');
    await theoryLessonUpdatePage.save();
    expect(await theoryLessonUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await theoryLessonComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last TheoryLesson', async () => {
    const nbButtonsBeforeDelete = await theoryLessonComponentsPage.countDeleteButtons();
    await theoryLessonComponentsPage.clickOnLastDeleteButton();

    theoryLessonDeleteDialog = new TheoryLessonDeleteDialog();
    expect(await theoryLessonDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.theoryLesson.delete.question');
    await theoryLessonDeleteDialog.clickOnConfirmButton();

    expect(await theoryLessonComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
