import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cc-panel',
  templateUrl: './ccpanel.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class CCPanel implements AfterViewInit {

  @Input() icon: string;
  @Input() title: string;

  @Input() subTitle: string;
  @Input() subTitleCondition = true;

  @Input() collapsable = false;
  @Input() collapseBody = false;
  @Input() dropDownLabel = 'Actions';
  @Output() onHeaderDblClick: EventEmitter<any> = new EventEmitter();

  public showDropDown = false;

  constructor() { };

  ngAfterViewInit(): void {
    if (!this.collapsable) {
      this.collapseBody = false;
    }
  }

  @ViewChild('dropdownmenu')
  set dropDownElements(element: any) {
    if (element) {
      this.showDropDown = element.nativeElement.childElementCount > 0;
    }
  }

  public doHeaderDblClick(): void {
    this.onHeaderDblClick.emit();
  }
}
