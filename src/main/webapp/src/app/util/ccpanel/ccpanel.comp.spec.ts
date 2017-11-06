import { DebugElement } from "@angular/core";
import { async, TestBed, ComponentFixture } from "@angular/core/testing";
import { By } from "@angular/platform-browser";

import { CCPanel } from "./ccpanel.comp";

describe('cc-panel', () => {

  let fixture: ComponentFixture<CCPanel>;
  let comp: CCPanel;

  let titleEl: DebugElement;
  let subtitleEl: DebugElement;
  let iconEl: DebugElement;
  let collapseButtonEl: DebugElement;
  let dropdownButtonEl: DebugElement;

  const expectedTitle = 'Panel Title';
  const expectedIcon = 'fa-file-text';
  const expectedSubtitle = 'Panel Subtitle';
  const expectedDropdownLabel = 'Dropdown Label';

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CCPanel]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CCPanel);
    comp = fixture.componentInstance;

    comp.title = expectedTitle;
    comp.subTitle = expectedSubtitle;
    comp.icon = expectedIcon;
    comp.collapsable = true;
    comp.dropDownLabel = expectedDropdownLabel;

    fixture.detectChanges();

    titleEl = fixture.debugElement.query(By.css('div.panel-heading'));
    subtitleEl = fixture.debugElement.query(By.css('.panel-heading small'));
    iconEl = fixture.debugElement.query(By.css('.panel-heading i'));
    collapseButtonEl = fixture.debugElement.query(By.css('.panel-heading button.collapse-toggle'));
    dropdownButtonEl = fixture.debugElement.query(By.css('.panel-heading button.dropdown-toggle'));
  });

  it('should show the title', () => {
    expect(titleEl.nativeElement.textContent).toContain(expectedTitle);
  });

  it('shoud show the icon', () => {
    expect(iconEl.nativeElement.classList).toContain(expectedIcon);
  });

  it('should show the subtitle', () => {
    expect(subtitleEl.nativeElement.textContent).toContain(expectedSubtitle);
  });

  it('should have a collapse toggle button', () => {
    expect(comp.collapseBody).toBe(false);
    collapseButtonEl.triggerEventHandler('click', null);
    expect(comp.collapseBody).toBe(true);
  });

  it('should have a named dropdown toggle button', () => {
    expect(dropdownButtonEl.nativeElement.textContent).toContain(expectedDropdownLabel);
  });

  it('should trigger an event when double clicked', () => {
    let triggered = false;
    comp.onHeaderDblClick.subscribe(() => triggered = true);
    titleEl.triggerEventHandler('dblclick', null);
    expect(triggered).toBe(true);
  });

});
