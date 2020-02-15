import { element, by, ElementFinder } from 'protractor';

export class TeachingDiagramComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-teaching-diagram div table .btn-danger'));
  title = element.all(by.css('jhi-teaching-diagram div h2#page-heading span')).first();

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

export class TeachingDiagramUpdatePage {
  pageTitle = element(by.id('jhi-teaching-diagram-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  basicInput = element(by.id('field_basic'));
  advancedInput = element(by.id('field_advanced'));
  performanceInput = element(by.id('field_performance'));
  independenceInput = element(by.id('field_independence'));
  overlandInput = element(by.id('field_overland'));
  autobahnInput = element(by.id('field_autobahn'));
  nightInput = element(by.id('field_night'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setBasicInput(basic) {
    await this.basicInput.sendKeys(basic);
  }

  async getBasicInput() {
    return await this.basicInput.getAttribute('value');
  }

  async setAdvancedInput(advanced) {
    await this.advancedInput.sendKeys(advanced);
  }

  async getAdvancedInput() {
    return await this.advancedInput.getAttribute('value');
  }

  async setPerformanceInput(performance) {
    await this.performanceInput.sendKeys(performance);
  }

  async getPerformanceInput() {
    return await this.performanceInput.getAttribute('value');
  }

  async setIndependenceInput(independence) {
    await this.independenceInput.sendKeys(independence);
  }

  async getIndependenceInput() {
    return await this.independenceInput.getAttribute('value');
  }

  async setOverlandInput(overland) {
    await this.overlandInput.sendKeys(overland);
  }

  async getOverlandInput() {
    return await this.overlandInput.getAttribute('value');
  }

  async setAutobahnInput(autobahn) {
    await this.autobahnInput.sendKeys(autobahn);
  }

  async getAutobahnInput() {
    return await this.autobahnInput.getAttribute('value');
  }

  async setNightInput(night) {
    await this.nightInput.sendKeys(night);
  }

  async getNightInput() {
    return await this.nightInput.getAttribute('value');
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

export class TeachingDiagramDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-teachingDiagram-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-teachingDiagram'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
