import { element, by, ElementFinder } from 'protractor';

export class TimeSlotComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-time-slot div table .btn-danger'));
  title = element.all(by.css('jhi-time-slot div h2#page-heading span')).first();

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

export class TimeSlotUpdatePage {
  pageTitle = element(by.id('jhi-time-slot-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  beginInput = element(by.id('field_begin'));
  endInput = element(by.id('field_end'));
  daySelect = element(by.id('field_day'));
  nightInput = element(by.id('field_night'));
  teacherSelect = element(by.id('field_teacher'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setBeginInput(begin) {
    await this.beginInput.sendKeys(begin);
  }

  async getBeginInput() {
    return await this.beginInput.getAttribute('value');
  }

  async setEndInput(end) {
    await this.endInput.sendKeys(end);
  }

  async getEndInput() {
    return await this.endInput.getAttribute('value');
  }

  async setDaySelect(day) {
    await this.daySelect.sendKeys(day);
  }

  async getDaySelect() {
    return await this.daySelect.element(by.css('option:checked')).getText();
  }

  async daySelectLastOption(timeout?: number) {
    await this.daySelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  getNightInput(timeout?: number) {
    return this.nightInput;
  }

  async teacherSelectLastOption(timeout?: number) {
    await this.teacherSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async teacherSelectOption(option) {
    await this.teacherSelect.sendKeys(option);
  }

  getTeacherSelect(): ElementFinder {
    return this.teacherSelect;
  }

  async getTeacherSelectedOption() {
    return await this.teacherSelect.element(by.css('option:checked')).getText();
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

export class TimeSlotDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-timeSlot-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-timeSlot'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
