import { Component, Input, ViewEncapsulation, Output, EventEmitter } from "@angular/core";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
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
  @Output() onHeaderDblClick: EventEmitter<any> = new EventEmitter();

  constructor() {
  };


  private doHeaderDblClick(): void {
    this.onHeaderDblClick.emit();
  }
}
