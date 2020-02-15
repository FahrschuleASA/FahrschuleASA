// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { LocationComponentsPage, LocationDeleteDialog, LocationUpdatePage } from './location.page-object';

const expect = chai.expect;

describe('Location e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let locationComponentsPage: LocationComponentsPage;
  let locationUpdatePage: LocationUpdatePage;
  let locationDeleteDialog: LocationDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Locations', async () => {
    await navBarPage.goToEntity('location');
    locationComponentsPage = new LocationComponentsPage();
    await browser.wait(ec.visibilityOf(locationComponentsPage.title), 5000);
    expect(await locationComponentsPage.getTitle()).to.eq('fahrschuleAsaApp.location.home.title');
  });

  it('should load create Location page', async () => {
    await locationComponentsPage.clickOnCreateButton();
    locationUpdatePage = new LocationUpdatePage();
    expect(await locationUpdatePage.getPageTitle()).to.eq('fahrschuleAsaApp.location.home.createOrEditLabel');
    await locationUpdatePage.cancel();
  });

  it('should create and save Locations', async () => {
    const nbButtonsBeforeCreate = await locationComponentsPage.countDeleteButtons();

    await locationComponentsPage.clickOnCreateButton();
    await promise.all([
      locationUpdatePage.setLongitudeInput('5'),
      locationUpdatePage.setLatitudeInput('5'),
      locationUpdatePage.setTownInput('town'),
      locationUpdatePage.setStreetInput('street'),
      locationUpdatePage.setPostalInput('postal'),
      locationUpdatePage.setHouseNumberInput('5'),
      locationUpdatePage.setCountryInput('country'),
      locationUpdatePage.setAdditionalInput('additional')
    ]);
    expect(await locationUpdatePage.getLongitudeInput()).to.eq('5', 'Expected longitude value to be equals to 5');
    expect(await locationUpdatePage.getLatitudeInput()).to.eq('5', 'Expected latitude value to be equals to 5');
    expect(await locationUpdatePage.getTownInput()).to.eq('town', 'Expected Town value to be equals to town');
    expect(await locationUpdatePage.getStreetInput()).to.eq('street', 'Expected Street value to be equals to street');
    expect(await locationUpdatePage.getPostalInput()).to.eq('postal', 'Expected Postal value to be equals to postal');
    expect(await locationUpdatePage.getHouseNumberInput()).to.eq('5', 'Expected houseNumber value to be equals to 5');
    expect(await locationUpdatePage.getCountryInput()).to.eq('country', 'Expected Country value to be equals to country');
    expect(await locationUpdatePage.getAdditionalInput()).to.eq('additional', 'Expected Additional value to be equals to additional');
    await locationUpdatePage.save();
    expect(await locationUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await locationComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Location', async () => {
    const nbButtonsBeforeDelete = await locationComponentsPage.countDeleteButtons();
    await locationComponentsPage.clickOnLastDeleteButton();

    locationDeleteDialog = new LocationDeleteDialog();
    expect(await locationDeleteDialog.getDialogTitle()).to.eq('fahrschuleAsaApp.location.delete.question');
    await locationDeleteDialog.clickOnConfirmButton();

    expect(await locationComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
