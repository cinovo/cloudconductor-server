import { Component, Input } from '@angular/core';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
    selector: 'cc-pagination-info',
    templateUrl: './ccpaginationinfo.comp.html',
    standalone: false
})
export class CCPaginationInfoComponent {

  @Input() start: number;
  @Input() end: number;
  @Input() totalCount: number;

}
