import { DebugElement, Type } from "@angular/core";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { By } from "@angular/platform-browser";
import { Router } from "@angular/router";

import { Observable } from "rxjs/Observable";

import { AlertService } from "../util/alert/alert.service";
import { ServiceOverview } from "./service.overview.comp";
import { Service, ServiceHttpService } from "../util/http/service.http.service";
import { CCTitle } from "../util/cctitle/cctitle.comp";
import { CCFilter } from "../util/ccfilter/ccfilter.comp";
import { RouterLinkStubDirective, QueryParamStubDirective, RouterStub } from "../../testing/router-stubs";
import { CCPanel } from "../util/ccpanel/ccpanel.comp";
import { ConfirmationPopoverModule } from "angular-confirmation-popover";
import { AlertStubService } from "../util/alert/alert-stub";
import { ConfirmationPopoverStubDirective } from "../../testing/confirmation-popover-stub";
import { checkLoadingMessage } from "../../testing/test-helper";

const expectedServices = [{ name: 'Testservice', packages: [] }];

class ServicesStub {
  getServices(): Observable<Service[]>{
    return Observable.of(expectedServices);
  }
}

describe('service-overview', () => {
  let comp: ServiceOverview;
  let fixture: ComponentFixture<ServiceOverview>;
  let servicesStub: ServicesStub;
  let alertStub: AlertStubService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ CCTitle, CCPanel, CCFilter, ServiceOverview,
        RouterLinkStubDirective, QueryParamStubDirective, ConfirmationPopoverStubDirective],
      providers: [
        { provide: ServiceHttpService, useClass: ServicesStub },
        { provide: Router, useClass: RouterStub },
        { provide: AlertService, useClass: AlertStubService }
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(ServiceOverview);
      comp = fixture.componentInstance;
      servicesStub = fixture.debugElement.injector.get(ServiceHttpService);
      alertStub = fixture.debugElement.injector.get(AlertService);
    });
  }));

  it('should show loading message', checkLoadingMessage(ServiceOverview, ServiceHttpService, 'getServices'));

  it('should display services in table rows', async(() => {
    const spy = spyOn(servicesStub, 'getServices').and.callThrough();

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(spy.calls.count()).toBe(1);
      spy.calls.reset();
      const tableRows = fixture.debugElement.queryAll(By.css('table tr'));
      expect(tableRows.length).toBe(expectedServices.length + 1);
    });
  }));

  it('should display warning if there are no services', async(() => {
    const spy = spyOn(servicesStub, 'getServices').and.returnValue(Observable.of([]));

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(spy.calls.count()).toBe(1);
      const alertEle = fixture.debugElement.query(By.css('.alert-warning'));
      expect(alertEle).not.toBeNull();
      expect(alertEle.nativeElement.textContent).toContain('There are currently no Services defined');
    });
  }));

  it('should create error alert when services can not be retrieved', async(() => {
    const spy = spyOn(servicesStub, 'getServices').and.returnValue(Observable.throw({}));
    const alertSpy = spyOn(alertStub, 'danger').and.callThrough();

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      fixture.detectChanges();
      expect(spy.calls.count()).toBe(1);
      expect(alertSpy.calls.count()).toBe(1);
    });
  }));

  it('should navigate to service when double clicked', async(() => {
    const router = fixture.debugElement.injector.get(Router);
    const spy = spyOn(router, 'navigate').and.callThrough();

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      fixture.detectChanges();

      const rowEls = fixture.debugElement.queryAll(By.css('tbody tr'));
      expect(rowEls.length).toBe(expectedServices.length);
      rowEls.forEach((rowEl, index) => {
        rowEl.nativeElement.dispatchEvent(new Event('dblclick'));
        expect(spy.calls.mostRecent().args[0]).toEqual(['service', expectedServices[index].name]);
      });
    });
  }));

  it('should navigate to service when clicked on edit button', () => {
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      const editButtonEls = fixture.debugElement.queryAll(By.directive(RouterLinkStubDirective))
                              .filter(ele => ele.nativeElement.classList.contains('edit'));

      expect(editButtonEls.length).toBe(expectedServices.length);
      editButtonEls.forEach((editBtn, index) => {
        const dir = editBtn.injector.get(RouterLinkStubDirective);
        editBtn.triggerEventHandler('click', null);
        expect(dir.navigatedTo).toEqual([expectedServices[index].name]);
      });
    });
  });

  it('should trigger confirm popover when clicked on delete button', () => {
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const deleteButtonEls = fixture.debugElement.queryAll(By.directive(ConfirmationPopoverStubDirective))
                                                  .filter((buttonEl) => buttonEl.nativeElement.classList.contains('delete'));
      expect(deleteButtonEls.length).toBe(expectedServices.length);
      deleteButtonEls.forEach((deleteButtonEl, index) => {
        const dir = deleteButtonEl.injector.get(ConfirmationPopoverStubDirective);
        const spy = spyOn(dir, 'onClick').and.callThrough();
        deleteButtonEl.triggerEventHandler('click', null);
        expect(spy.calls.count()).toBe(1);
      });
    });
  });

});
