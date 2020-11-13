import { Alert } from "./alert.service";
import { Observable ,  Subject } from "rxjs";

export const expectedAlerts: Alert[] = [{type: 'warning', msg: 'Alert One', autoFadeOut: true}, {type: 'danger', msg: 'Alert Two', autoFadeOut: true}];

export class AlertStubService {
  public getAlerts(): Observable<Alert> {
    let sub = new Subject<Alert>();

    for (let i = 0; i < expectedAlerts.length; i++) {
      sub.next(expectedAlerts[i]);
    }
    return sub.asObservable();
  }

  public danger(message: string): void {
    return;
  }
}
