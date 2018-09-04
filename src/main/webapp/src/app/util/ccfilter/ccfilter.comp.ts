import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

type FilterMode = 'text' | 'number' | 'select';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cc-filter',
  templateUrl: './ccfilter.comp.html'
})
export class CCFilter implements OnInit {

  @Input() label = 'Search';
  @Input() mode: FilterMode = 'text';
  @Input() data: Array<string> | Array<any> = [];
  @Input() dataField: string;

  get selected() {
    return this._query;
  }

  @Input() set selected(val: string) {
    this._query = val;
  }

  @Output() onQueryChange: EventEmitter<string> = new EventEmitter();

  private _query = '';

  constructor() {
  };

  ngOnInit(): void {
  }

  get query(): string {
    return this._query;
  }

  set query(value: string) {
    this._query = value;
    this.onQueryChange.emit(this._query);
  }

}
