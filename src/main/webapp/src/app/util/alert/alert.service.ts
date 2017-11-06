import { Injectable } from '@angular/core';

type AlertType = 'success' | 'info' | 'warning' | 'danger';

export interface Alert {
  type: AlertType,
  msg: string
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

  public getAlerts(): Alert[] {
    return this.alerts;
  }

  public closeAlert(index: number): void {
    this.alerts[index] = null;
  }

  private addAlert(type: AlertType, msg: string): void {
    let index = this.alerts.indexOf(null);
    if (index > -1) {
      this.alerts[index] = {type: type, msg: msg};
    }else {
      index = this.alerts.push({type, msg}) - 1;
    }
    setTimeout(() => {this.closeAlert(index)}, 5000);
  }

  public success(message: string): void {
    this.addAlert('success', message);
  }

  public info(message: string): void {
    this.addAlert('info', message);
  }

  public warning(message: string): void {
    this.addAlert('warning', message);
  }

  public danger(message: string): void {
    this.addAlert('danger', message);
  }
}
