import { element, by, ElementFinder } from 'protractor';

export class StudentComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-student div table .btn-danger'));
  title = element.all(by.css('jhi-student div h2#page-heading span')).first();

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

export class StudentUpdatePage {
  pageTitle = element(by.id('jhi-student-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  categorySelect = element(by.id('field_category'));
  readyForTheoryInput = element(by.id('field_readyForTheory'));
  wantedLessonsInput = element(by.id('field_wantedLessons'));
  changedPreferencesInput = element(by.id('field_changedPreferences'));
  teachingDiagramSelect = element(by.id('field_teachingDiagram'));
  theoryLessonsSelect = element(by.id('field_theoryLessons'));
  optionalDrivingLessonsSelect = element(by.id('field_optionalDrivingLessons'));
  teacherSelect = element(by.id('field_teacher'));

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

  getReadyForTheoryInput(timeout?: number) {
    return this.readyForTheoryInput;
  }
  async setWantedLessonsInput(wantedLessons) {
    await this.wantedLessonsInput.sendKeys(wantedLessons);
  }

  async getWantedLessonsInput() {
    return await this.wantedLessonsInput.getAttribute('value');
  }

  getChangedPreferencesInput(timeout?: number) {
    return this.changedPreferencesInput;
  }

  async teachingDiagramSelectLastOption(timeout?: number) {
    await this.teachingDiagramSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async teachingDiagramSelectOption(option) {
    await this.teachingDiagramSelect.sendKeys(option);
  }

  getTeachingDiagramSelect(): ElementFinder {
    return this.teachingDiagramSelect;
  }

  async getTeachingDiagramSelectedOption() {
    return await this.teachingDiagramSelect.element(by.css('option:checked')).getText();
  }

  async theoryLessonsSelectLastOption(timeout?: number) {
    await this.theoryLessonsSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async theoryLessonsSelectOption(option) {
    await this.theoryLessonsSelect.sendKeys(option);
  }

  getTheoryLessonsSelect(): ElementFinder {
    return this.theoryLessonsSelect;
  }

  async getTheoryLessonsSelectedOption() {
    return await this.theoryLessonsSelect.element(by.css('option:checked')).getText();
  }

  async optionalDrivingLessonsSelectLastOption(timeout?: number) {
    await this.optionalDrivingLessonsSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async optionalDrivingLessonsSelectOption(option) {
    await this.optionalDrivingLessonsSelect.sendKeys(option);
  }

  getOptionalDrivingLessonsSelect(): ElementFinder {
    return this.optionalDrivingLessonsSelect;
  }

  async getOptionalDrivingLessonsSelectedOption() {
    return await this.optionalDrivingLessonsSelect.element(by.css('option:checked')).getText();
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

export class StudentDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-student-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-student'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
