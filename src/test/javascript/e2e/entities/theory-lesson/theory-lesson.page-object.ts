import { element, by, ElementFinder } from 'protractor';

export class TheoryLessonComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-theory-lesson div table .btn-danger'));
  title = element.all(by.css('jhi-theory-lesson div h2#page-heading span')).first();

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

export class TheoryLessonUpdatePage {
  pageTitle = element(by.id('jhi-theory-lesson-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  subjectInput = element(by.id('field_subject'));
  teacherSelect = element(by.id('field_teacher'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setSubjectInput(subject) {
    await this.subjectInput.sendKeys(subject);
  }

  async getSubjectInput() {
    return await this.subjectInput.getAttribute('value');
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

export class TheoryLessonDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-theoryLesson-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-theoryLesson'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
