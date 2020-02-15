import { element, by, ElementFinder } from 'protractor';

export class PreferenceComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-preference div table .btn-danger'));
  title = element.all(by.css('jhi-preference div h2#page-heading span')).first();

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

export class PreferenceUpdatePage {
  pageTitle = element(by.id('jhi-preference-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  pickupSelect = element(by.id('field_pickup'));
  destinationSelect = element(by.id('field_destination'));
  timeSlotSelect = element(by.id('field_timeSlot'));
  studentSelect = element(by.id('field_student'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async pickupSelectLastOption(timeout?: number) {
    await this.pickupSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async pickupSelectOption(option) {
    await this.pickupSelect.sendKeys(option);
  }

  getPickupSelect(): ElementFinder {
    return this.pickupSelect;
  }

  async getPickupSelectedOption() {
    return await this.pickupSelect.element(by.css('option:checked')).getText();
  }

  async destinationSelectLastOption(timeout?: number) {
    await this.destinationSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async destinationSelectOption(option) {
    await this.destinationSelect.sendKeys(option);
  }

  getDestinationSelect(): ElementFinder {
    return this.destinationSelect;
  }

  async getDestinationSelectedOption() {
    return await this.destinationSelect.element(by.css('option:checked')).getText();
  }

  async timeSlotSelectLastOption(timeout?: number) {
    await this.timeSlotSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async timeSlotSelectOption(option) {
    await this.timeSlotSelect.sendKeys(option);
  }

  getTimeSlotSelect(): ElementFinder {
    return this.timeSlotSelect;
  }

  async getTimeSlotSelectedOption() {
    return await this.timeSlotSelect.element(by.css('option:checked')).getText();
  }

  async studentSelectLastOption(timeout?: number) {
    await this.studentSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async studentSelectOption(option) {
    await this.studentSelect.sendKeys(option);
  }

  getStudentSelect(): ElementFinder {
    return this.studentSelect;
  }

  async getStudentSelectedOption() {
    return await this.studentSelect.element(by.css('option:checked')).getText();
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

export class PreferenceDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-preference-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-preference'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
