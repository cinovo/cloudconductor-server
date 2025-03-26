import { Component, Input } from '@angular/core';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
    selector: 'cc-pagination',
    templateUrl: './ccpagination.comp.html',
    styleUrls: ['./ccpagination.comp.scss'],
    standalone: false
})
export class CCPagination {

  @Input() pageCount: number;
  @Input() pages: number[];
  @Input() currentPage: number;

}
