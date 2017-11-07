import { Type } from "@angular/core";
import { TestBed } from "@angular/core/testing";
import { By } from "@angular/platform-browser";

import { Observable } from "rxjs/Observable";

export function checkLoadingMessage<C,S>(component: Type<C>, serviceType: Type<S>, methodName: string) {
  return () => {
    const fixture = TestBed.createComponent(component);
    const service = fixture.debugElement.injector.get(serviceType) as any;
    const spy = spyOn(service, methodName).and.returnValue(Observable.of([]).delay(0));

    fixture.detectChanges();
    const loadingMsgEl = fixture.debugElement.query(By.css('.alert-info'));
    expect(loadingMsgEl.nativeElement.textContent).toContain('Loading');

    fixture.whenStable().then(() => {
      const loadingMsgElAfter = fixture.debugElement.query(By.css('.alert-info'));
      expect(loadingMsgElAfter).toBeNull();
    });
  };
};
