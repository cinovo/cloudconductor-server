import {
  Component,
  Input,
  ViewEncapsulation,
  Output,
  EventEmitter,
  ViewChildren,
  AfterContentInit, ViewChild, AfterContentChecked, AfterViewInit
} from "@angular/core";

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
  @Input() subTitleCondition:boolean = true;

  @Input() collapsable: boolean = false;
  @Input() collapseBody: boolean = false;
  @Input() dropDownLabel: string = "Actions";
  @Output() onHeaderDblClick: EventEmitter<any> = new EventEmitter();

  private showDropDown:boolean = false;

  constructor() {
  };

  ngAfterViewInit(): void {
    if (!this.collapsable) {
      this.collapseBody = false;
    }
  }

  @ViewChild('dropdownmenu')
  set dropDownElements(element:any) {
    if(element) {
      this.showDropDown = element.nativeElement.childElementCount > 0;
    }
  }

  private doHeaderDblClick(): void {
    this.onHeaderDblClick.emit();
  }
}
