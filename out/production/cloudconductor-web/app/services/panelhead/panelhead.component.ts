/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, Input, ViewEncapsulation, NO_ERRORS_SCHEMA } from "@angular/core";

@Component({
  moduleId: module.id,
  selector: 'cc-panel',
  templateUrl: 'html/panelhead.component.html',
  encapsulation: ViewEncapsulation.None
})
export class CCPanelComponent {

  @Input() icon: string;
  @Input() title: string;

  constructor() {
  };

}
