import { Component, ViewEncapsulation } from '@angular/core';

import { AlertService } from './alert.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'alert-area',
  templateUrl: './alert.comp.html',
  styleUrls: ['./alert.comp.scss']
})
export class AlertComponent {

  constructor(public alertService: AlertService) { };

}
