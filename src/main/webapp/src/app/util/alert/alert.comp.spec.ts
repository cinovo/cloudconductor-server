import { DebugElement } from "@angular/core";
import { async, TestBed, ComponentFixture } from "@angular/core/testing";
import { By } from "@angular/platform-browser";

import { AlertComponent } from "./alert.comp";
import { AlertService, Alert } from "./alert.service";
import { AlertStubService, expectedAlerts } from "./alert-stub";

describe('alert-area', () => {
  let fixture: ComponentFixture<AlertComponent>;
  let comp: AlertComponent;
  let alertService: AlertService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlertComponent],
      providers: [{ provide: AlertService, useClass: AlertStubService }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertComponent);
    comp = fixture.componentInstance;

    alertService = fixture.debugElement.injector.get(AlertService);
  });

  it('should show alerts', () => {
    const spy = spyOn(alertService, 'getAlerts').and.callThrough();

    fixture.detectChanges();

    expect(spy.calls.count()).toBeGreaterThan(0);
    const alertEls = fixture.debugElement.queryAll(By.css('div.alert'));
    expect(alertEls.length).toBe(expectedAlerts.length);
    alertEls.forEach((alertEl: DebugElement, index) => {
      const expectedAlert = expectedAlerts[index];
      expect(alertEl.nativeElement.classList).toContain(`alert-${expectedAlert.type}`)
      expect(alertEl.nativeElement.textContent).toContain(expectedAlert.msg);
    });
  });

  it('should call close when button is clicked', () => {
    const spy = spyOn(alertService, 'closeAlert').and.callThrough();

    fixture.detectChanges();

    const closeButtonEls = fixture.debugElement.queryAll(By.css(`button.close`));
    expect(closeButtonEls.length).toBe(expectedAlerts.length);
    closeButtonEls.forEach((closeButton, index) => {
      closeButton.triggerEventHandler('click', null);
      expect(spy.calls.mostRecent().args[0]).toBe(index);
    });
  });

});
