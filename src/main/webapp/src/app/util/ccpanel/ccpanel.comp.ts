import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, Output, ViewChild, ViewEncapsulation} from '@angular/core';

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

  @ViewChild('dropdownmenu', { static: true })
  public dropDownElement: ElementRef;

  public showDropDown: boolean = false;

  constructor(private readonly cdRef: ChangeDetectorRef) { }

  ngAfterViewInit(): void {
    if (!this.collapsable) {
      this.collapseBody = false;
    }
    this.showDropDown = this.dropDownElement.nativeElement && this.dropDownElement.nativeElement.children.length > 0;
    this.cdRef.detectChanges();
  }

  public doHeaderDblClick(): void {
    this.onHeaderDblClick.emit();
  }
}
