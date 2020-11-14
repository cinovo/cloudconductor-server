import { DebugElement } from "@angular/core";
import { TestBed, ComponentFixture, waitForAsync } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { By } from "@angular/platform-browser";

import { CCFilter } from "./ccfilter.comp";

describe('cc-filter', () => {

  let fixture: ComponentFixture<CCFilter>;
  let comp: CCFilter;

  const dataField = 'name';
  const expectedLabel = 'Filter Label';
  const expectedData = ['One', 'Two', 'Three'];
  const expectedComplexData = [{ name: 'One' }, { name: 'Two' }, { name: 'Three' }];

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [CCFilter]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CCFilter);
    comp = fixture.componentInstance;
  });

  it('should have a label', () => {
    comp.label = expectedLabel;

    fixture.detectChanges();

    const labelEl = fixture.debugElement.query(By.css('label'));
    expect(labelEl.nativeElement.textContent).toContain(expectedLabel);
  });

  it('should have a text mode', () => {
    comp.mode = 'text';

    fixture.detectChanges();

    const inputEl = fixture.debugElement.query(By.css('input'));
    expect(inputEl).not.toBe(null);
  });

  it('should have a select mode', () => {
    comp.mode = 'select';
    fixture.detectChanges();

    const selectEl = fixture.debugElement.query(By.css('select'));
    expect(selectEl).not.toBe(null);
  });

  it('should show simple string options in text mode', () => {
    comp.data = expectedData;
    comp.mode = 'text';

    fixture.detectChanges();

    const optionEls = fixture.debugElement.queryAll(By.css('option'));
    optionEls.forEach((optionEl, index) => {
      expect(optionEl.nativeElement.value).toBe(expectedData[index]);
    });
  });

  it('should show simple string options in select mode', () => {
    comp.data = expectedData;
    comp.mode = 'select';

    fixture.detectChanges();

    const optionEls = fixture.debugElement.queryAll(By.css('option'));
    optionEls.slice(1).forEach((optionEl, index) => {
      expect(optionEl.nativeElement.value).toBe(expectedData[index]);
      expect(optionEl.nativeElement.textContent).toBe(expectedData[index]);
    });
  });

  it('should show complex options in text mode', () => {
    comp.data = expectedComplexData;
    comp.mode = 'text';
    comp.dataField = dataField;

    fixture.detectChanges();

    const optionEls = fixture.debugElement.queryAll(By.css('option'));
    optionEls.forEach((optionEl, index) => expect(optionEl.nativeElement.value).toBe(expectedComplexData[index][dataField]));
  });

  it('should show complex options in select mode', () => {
    comp.data = expectedComplexData;
    comp.mode = 'select';
    comp.dataField = 'name';

    fixture.detectChanges();

    const optionEls = fixture.debugElement.queryAll(By.css('option'));
    optionEls.slice(1).forEach((optionEl, index) => {
      expect(optionEl.nativeElement.value).toBe(expectedComplexData[index][dataField]);
      expect(optionEl.nativeElement.textContent).toBe(expectedComplexData[index][dataField]);
    });
  });

  it('should trigger event on text input', () => {
    let triggered = false;
    comp.mode = 'text';
    comp.onQueryChange.subscribe(() => triggered = true);

    fixture.detectChanges();

    const inputEl = fixture.debugElement.query(By.css('input'));
    inputEl.nativeElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(triggered).toBe(true);
  });

  it('should trigger event on selection', () => {
    let triggered = false;
    comp.mode = 'select';
    comp.onQueryChange.subscribe(() => triggered = true);

    fixture.detectChanges();
    const selectEl = fixture.debugElement.query(By.css('select'));
    selectEl.nativeElement.dispatchEvent(new Event('change'));

    fixture.detectChanges();

    expect(triggered).toBe(true);
  });

});
