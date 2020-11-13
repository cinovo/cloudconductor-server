import { Injectable } from '@angular/core';
import { Subject ,  Observable } from "rxjs";

type AlertType = 'success' | 'info' | 'warning' | 'danger';

export interface Alert {
  type: AlertType,
  msg: string;
  autoFadeOut: boolean;
}

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Injectable()
export class AlertService {

  private alerts: Alert[] = [];

  private subject = new Subject<Alert>();

  constructor() {
  }

  public getAlerts(): Observable<Alert> {
    return this.subject.asObservable();
  }

  public closeAlert(index: number): void {
    this.alerts[index] = null;
  }

  private addAlert(type: AlertType, msg: string, autoFadeOut: boolean = false): void {
    this.subject.next(<Alert>{ type: type, msg: msg, autoFadeOut: autoFadeOut});
  }

  public success(message: string): void {
    this.addAlert('success', message, true);
  }

  public info(message: string): void {
    this.addAlert('info', message, true);
  }

  public warning(message: string): void {
    this.addAlert('warning', message, true);
  }

  public danger(message: string): void {
    this.addAlert('danger', message);
  }

  public clear() {
    this.subject.next();
  }
}
