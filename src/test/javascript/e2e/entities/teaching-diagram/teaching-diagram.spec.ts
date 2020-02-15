// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { TeachingDiagramComponentsPage, TeachingDiagramDeleteDialog, TeachingDiagramUpdatePage } from './teaching-diagram.page-object';

const expect = chai.expect;

describe('TeachingDiagram e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let teachingDiagramComponentsPage: TeachingDiagramComponentsPage;
  let teachingDiagramUpdatePage: TeachingDiagramUpdatePage;
  let teachingDiagramDeleteDialog: TeachingDiagramDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load TeachingDiagrams', async () => {
    await navBarPage.goToEntity('teaching-diagram');
    teachingDiagramComponentsPage = new TeachingDiagramComponentsPage();
    await browser.wait(ec.visibilityOf(teachingDiagramComponentsPage.title), 5000);
    expect(await teachingDiagramComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.teachingDiagram.home.title');
  });

  it('should load create TeachingDiagram page', async () => {
    await teachingDiagramComponentsPage.clickOnCreateButton();
    teachingDiagramUpdatePage = new TeachingDiagramUpdatePage();
    expect(await teachingDiagramUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.teachingDiagram.home.createOrEditLabel');
    await teachingDiagramUpdatePage.cancel();
  });

  it('should create and save TeachingDiagrams', async () => {
    const nbButtonsBeforeCreate = await teachingDiagramComponentsPage.countDeleteButtons();

    await teachingDiagramComponentsPage.clickOnCreateButton();
    await promise.all([
      teachingDiagramUpdatePage.setBasicInput('5'),
      teachingDiagramUpdatePage.setAdvancedInput('5'),
      teachingDiagramUpdatePage.setPerformanceInput('5'),
      teachingDiagramUpdatePage.setIndependenceInput('5'),
      teachingDiagramUpdatePage.setOverlandInput('5'),
      teachingDiagramUpdatePage.setAutobahnInput('5'),
      teachingDiagramUpdatePage.setNightInput('5')
    ]);
    expect(await teachingDiagramUpdatePage.getBasicInput()).to.eq('5', 'Expected basic value to be equals to 5');
    expect(await teachingDiagramUpdatePage.getAdvancedInput()).to.eq('5', 'Expected advanced value to be equals to 5');
    expect(await teachingDiagramUpdatePage.getPerformanceInput()).to.eq('5', 'Expected performance value to be equals to 5');
    expect(await teachingDiagramUpdatePage.getIndependenceInput()).to.eq('5', 'Expected independence value to be equals to 5');
    expect(await teachingDiagramUpdatePage.getOverlandInput()).to.eq('5', 'Expected overland value to be equals to 5');
    expect(await teachingDiagramUpdatePage.getAutobahnInput()).to.eq('5', 'Expected autobahn value to be equals to 5');
    expect(await teachingDiagramUpdatePage.getNightInput()).to.eq('5', 'Expected night value to be equals to 5');
    await teachingDiagramUpdatePage.save();
    expect(await teachingDiagramUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await teachingDiagramComponentsPage.countDeleteButtons()).to.eq(
      nbButtonsBeforeCreate + 1,
      'Expected one more entry in the table'
    );
  });

  it('should delete last TeachingDiagram', async () => {
    const nbButtonsBeforeDelete = await teachingDiagramComponentsPage.countDeleteButtons();
    await teachingDiagramComponentsPage.clickOnLastDeleteButton();

    teachingDiagramDeleteDialog = new TeachingDiagramDeleteDialog();
    expect(await teachingDiagramDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.teachingDiagram.delete.question');
    await teachingDiagramDeleteDialog.clickOnConfirmButton();

    expect(await teachingDiagramComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
