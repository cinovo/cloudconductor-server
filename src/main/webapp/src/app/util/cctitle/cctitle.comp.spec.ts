import { DebugElement } from "@angular/core";
import { async, TestBed, ComponentFixture } from "@angular/core/testing";
import { By } from "@angular/platform-browser";

import { CCTitle } from "./cctitle.comp";

describe('cc-title', () => {
  let fixture: ComponentFixture<CCTitle>;
  let comp: CCTitle;

  let titleEl: DebugElement;
  let subtitleEl: DebugElement;
  let iconEl: DebugElement;

  let expectedTitle: string;
  let expectedSubtitle: string;
  let expectedIcon: string;

  // async beforeEach()
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CCTitle]
    }).compileComponents();
  }));

  // synchronous beforeEach()
  beforeEach(() => {
    fixture = TestBed.createComponent(CCTitle);
    comp = fixture.componentInstance;

    expectedTitle = 'Title';
    expectedSubtitle = 'Subtitle';
    expectedIcon = 'fa-file';

    comp.title = expectedTitle;
    comp.subtitle = expectedSubtitle;
    comp.titleIcon = expectedIcon;

    fixture.detectChanges();

    titleEl = fixture.debugElement.query(By.css('h1.page-header'));
    subtitleEl = fixture.debugElement.query(By.css('h4'));
    iconEl = fixture.debugElement.query(By.css('h1.page-header i'));
  });

  it('should show the title', () => {
    expect(titleEl.nativeElement.textContent).toContain(expectedTitle);
  });

  it('should show the subtitle', () => {
    expect(subtitleEl.nativeElement.textContent).toContain(expectedSubtitle);
  });

  it('should show the icon', () => {
    expect(iconEl.nativeElement.classList).toContain(expectedIcon);
  });

});
