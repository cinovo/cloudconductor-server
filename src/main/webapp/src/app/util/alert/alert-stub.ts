import { Alert } from "./alert.service";

export const expectedAlerts: Alert[] = [{ type: 'warning', msg: 'Alert One' }, { type: 'danger', msg: 'Alert Two' }];

export class AlertStubService {
  public getAlerts(): Alert[] {
    return expectedAlerts;
  }

  public closeAlert(index: number): void {
    return;
  }

  public danger(message: string): void {
    return;
  }
}
