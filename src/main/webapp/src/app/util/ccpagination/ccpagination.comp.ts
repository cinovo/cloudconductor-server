import { Component, Input } from '@angular/core';

@Component({
  selector: 'cc-pagination',
  templateUrl: './ccpagination.comp.html'
})
export class CCPagination {

  @Input() start: number;
  @Input() end: number;
  @Input() totalCount: number;
  @Input() pageCount: number;
  @Input() pages: number[];
  @Input() currentPage: number;

}
