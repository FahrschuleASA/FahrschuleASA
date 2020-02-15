import { element, by, ElementFinder } from 'protractor';

export class DrivingLessonComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-driving-lesson div table .btn-danger'));
  title = element.all(by.css('jhi-driving-lesson div h2#page-heading span')).first();

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

export class DrivingLessonUpdatePage {
  pageTitle = element(by.id('jhi-driving-lesson-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  categorySelect = element(by.id('field_category'));
  lessonTypeSelect = element(by.id('field_lessonType'));
  driverSelect = element(by.id('field_driver'));
  missingSelect = element(by.id('field_missing'));
  timeSlotSelect = element(by.id('field_timeSlot'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setCategorySelect(category) {
    await this.categorySelect.sendKeys(category);
  }

  async getCategorySelect() {
    return await this.categorySelect.element(by.css('option:checked')).getText();
  }

  async categorySelectLastOption(timeout?: number) {
    await this.categorySelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setLessonTypeSelect(lessonType) {
    await this.lessonTypeSelect.sendKeys(lessonType);
  }

  async getLessonTypeSelect() {
    return await this.lessonTypeSelect.element(by.css('option:checked')).getText();
  }

  async lessonTypeSelectLastOption(timeout?: number) {
    await this.lessonTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async driverSelectLastOption(timeout?: number) {
    await this.driverSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async driverSelectOption(option) {
    await this.driverSelect.sendKeys(option);
  }

  getDriverSelect(): ElementFinder {
    return this.driverSelect;
  }

  async getDriverSelectedOption() {
    return await this.driverSelect.element(by.css('option:checked')).getText();
  }

  async missingSelectLastOption(timeout?: number) {
    await this.missingSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async missingSelectOption(option) {
    await this.missingSelect.sendKeys(option);
  }

  getMissingSelect(): ElementFinder {
    return this.missingSelect;
  }

  async getMissingSelectedOption() {
    return await this.missingSelect.element(by.css('option:checked')).getText();
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

export class DrivingLessonDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-drivingLesson-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-drivingLesson'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
