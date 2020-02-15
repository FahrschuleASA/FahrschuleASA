import { element, by, ElementFinder } from 'protractor';

export class LocationComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-location div table .btn-danger'));
  title = element.all(by.css('jhi-location div h2#page-heading span')).first();

  async clickOnCreateButton(timeout?: number) {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(timeout?: number) {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons() {
    return this.deleteButtons.count();
  }

  async getTitle() {
    return this.title.getAttribute('jhiTranslate');
  }
}

export class LocationUpdatePage {
  pageTitle = element(by.id('jhi-location-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  longitudeInput = element(by.id('field_longitude'));
  latitudeInput = element(by.id('field_latitude'));
  townInput = element(by.id('field_town'));
  streetInput = element(by.id('field_street'));
  postalInput = element(by.id('field_postal'));
  houseNumberInput = element(by.id('field_houseNumber'));
  countryInput = element(by.id('field_country'));
  additionalInput = element(by.id('field_additional'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setLongitudeInput(longitude) {
    await this.longitudeInput.sendKeys(longitude);
  }

  async getLongitudeInput() {
    return await this.longitudeInput.getAttribute('value');
  }

  async setLatitudeInput(latitude) {
    await this.latitudeInput.sendKeys(latitude);
  }

  async getLatitudeInput() {
    return await this.latitudeInput.getAttribute('value');
  }

  async setTownInput(town) {
    await this.townInput.sendKeys(town);
  }

  async getTownInput() {
    return await this.townInput.getAttribute('value');
  }

  async setStreetInput(street) {
    await this.streetInput.sendKeys(street);
  }

  async getStreetInput() {
    return await this.streetInput.getAttribute('value');
  }

  async setPostalInput(postal) {
    await this.postalInput.sendKeys(postal);
  }

  async getPostalInput() {
    return await this.postalInput.getAttribute('value');
  }

  async setHouseNumberInput(houseNumber) {
    await this.houseNumberInput.sendKeys(houseNumber);
  }

  async getHouseNumberInput() {
    return await this.houseNumberInput.getAttribute('value');
  }

  async setCountryInput(country) {
    await this.countryInput.sendKeys(country);
  }

  async getCountryInput() {
    return await this.countryInput.getAttribute('value');
  }

  async setAdditionalInput(additional) {
    await this.additionalInput.sendKeys(additional);
  }

  async getAdditionalInput() {
    return await this.additionalInput.getAttribute('value');
  }

  async save(timeout?: number) {
    await this.saveButton.click();
  }

  async cancel(timeout?: number) {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class LocationDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-location-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-location'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
