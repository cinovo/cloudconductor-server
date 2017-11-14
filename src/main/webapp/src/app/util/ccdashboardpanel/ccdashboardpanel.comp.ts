import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'cc-dashboard-panel',
  templateUrl: './ccdashboardpanel.comp.html',
  styleUrls: ['./ccdashboardpanel.comp.scss'],
})
export class CCDashboardPanel {

  @Input() icon: string;
  @Input() number: number;
  @Input() label: string;
  @Input() detailLink: string;
  @Input() minValue: number;

}
