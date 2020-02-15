import { element, by, ElementFinder } from 'protractor';

export class TeacherComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-teacher div table .btn-danger'));
  title = element.all(by.css('jhi-teacher div h2#page-heading span')).first();

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

export class TeacherUpdatePage {
  pageTitle = element(by.id('jhi-teacher-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  changedTimeSlotsInput = element(by.id('field_changedTimeSlots'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  getChangedTimeSlotsInput(timeout?: number) {
    return this.changedTimeSlotsInput;
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

export class TeacherDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-teacher-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-teacher'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
