// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { TimeSlotComponentsPage, TimeSlotDeleteDialog, TimeSlotUpdatePage } from './time-slot.page-object';

const expect = chai.expect;

describe('TimeSlot e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let timeSlotComponentsPage: TimeSlotComponentsPage;
  let timeSlotUpdatePage: TimeSlotUpdatePage;
  let timeSlotDeleteDialog: TimeSlotDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load TimeSlots', async () => {
    await navBarPage.goToEntity('time-slot');
    timeSlotComponentsPage = new TimeSlotComponentsPage();
    await browser.wait(ec.visibilityOf(timeSlotComponentsPage.title), 5000);
    expect(await timeSlotComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.timeSlot.home.title');
  });

  it('should load create TimeSlot page', async () => {
    await timeSlotComponentsPage.clickOnCreateButton();
    timeSlotUpdatePage = new TimeSlotUpdatePage();
    expect(await timeSlotUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.timeSlot.home.createOrEditLabel');
    await timeSlotUpdatePage.cancel();
  });

  it('should create and save TimeSlots', async () => {
    const nbButtonsBeforeCreate = await timeSlotComponentsPage.countDeleteButtons();

    await timeSlotComponentsPage.clickOnCreateButton();
    await promise.all([
      timeSlotUpdatePage.setBeginInput('5'),
      timeSlotUpdatePage.setEndInput('5'),
      timeSlotUpdatePage.daySelectLastOption(),
      timeSlotUpdatePage.teacherSelectLastOption()
    ]);
    expect(await timeSlotUpdatePage.getBeginInput()).to.eq('5', 'Expected begin value to be equals to 5');
    expect(await timeSlotUpdatePage.getEndInput()).to.eq('5', 'Expected end value to be equals to 5');
    const selectedNight = timeSlotUpdatePage.getNightInput();
    if (await selectedNight.isSelected()) {
      await timeSlotUpdatePage.getNightInput().click();
      expect(await timeSlotUpdatePage.getNightInput().isSelected(), 'Expected night not to be selected').to.be.false;
    } else {
      await timeSlotUpdatePage.getNightInput().click();
      expect(await timeSlotUpdatePage.getNightInput().isSelected(), 'Expected night to be selected').to.be.true;
    }
    await timeSlotUpdatePage.save();
    expect(await timeSlotUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await timeSlotComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last TimeSlot', async () => {
    const nbButtonsBeforeDelete = await timeSlotComponentsPage.countDeleteButtons();
    await timeSlotComponentsPage.clickOnLastDeleteButton();

    timeSlotDeleteDialog = new TimeSlotDeleteDialog();
    expect(await timeSlotDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.timeSlot.delete.question');
    await timeSlotDeleteDialog.clickOnConfirmButton();

    expect(await timeSlotComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
