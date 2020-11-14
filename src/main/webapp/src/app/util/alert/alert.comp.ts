import { Component } from '@angular/core';
import {animate, keyframes, query, stagger, style, transition, trigger} from '@angular/animations';

import { Alert, AlertService } from './alert.service';
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'alert-area',
  templateUrl: './alert.comp.html',
  styleUrls: ['./alert.comp.scss'],
  animations: [
    trigger('alertAnim', [
      transition('* => *', [
        query(':enter', style({opacity: 0}), {optional: true}),
        query(':enter', stagger('300ms', [
          animate('.6s ease-in', keyframes([
            style({opacity: 0, transform: 'translateY(-10%)', offset: 0}),
            style({opacity: 1, transform: 'translateY(0)', offset: 1.0}),
          ]))]), {optional: true}),
        query(':leave', stagger('300ms', [
          animate('.6s ease-out', keyframes([
            style({opacity: 1, transform: 'translateY(0)', offset: 0}),
            style({opacity: .5, transform: 'translateY(35px)', offset: 0.3}),
            style({opacity: 0, transform: 'translateY(-75%)', offset: 1.0}),
          ]))]), {optional: true})
      ])
    ])
  ]
})
export class AlertComponent {

  alerts: Alert[] = [];

  constructor(private alertService: AlertService) {
  }

  ngOnInit() {
    this.alertService.getAlerts().subscribe((alert: Alert) => {
      if (!alert) {
        // clear alerts when an empty alert is received
        this.alerts = [];
        return;
      }
      // add alert to array
      this.alerts.push(alert);
      if (alert.autoFadeOut) {
        setTimeout(() => {
          this.removeAlert(alert);
        }, 5000);
      }
    });
  }

  removeAlert(alert: Alert) {
    this.alerts = this.alerts.filter(x => x !== alert);
  }

}
