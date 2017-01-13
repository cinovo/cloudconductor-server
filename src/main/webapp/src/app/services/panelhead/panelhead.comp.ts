/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, Input, ViewEncapsulation } from "@angular/core";

@Component({
  moduleId: module.id,
  selector: 'cc-panel',
  templateUrl: 'html/panelhead.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class CCPanelComponent {

  @Input() icon: string;
  @Input() title: string;
  @Input() dropDownLabel: string = "Actions";

  constructor() {
  };

}
