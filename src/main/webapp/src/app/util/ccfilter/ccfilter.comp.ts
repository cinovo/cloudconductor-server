/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
import { Component, Input, EventEmitter, Output, AfterViewInit } from "@angular/core";

@Component({
  moduleId: module.id,
  selector: 'cc-filter',
  templateUrl: 'html/ccfilter.comp.html'
})
export class CCFilter implements AfterViewInit {

  @Input() label: string = "Search";
  @Input() mode: "text" | "select" = "text";
  @Input() data: Array<String> = [];
  @Input() dataField: string;

  @Input() selected: string;

  @Output() onQueryChange: EventEmitter<string> = new EventEmitter();

  private _query: string;

  constructor() {
  };

  ngAfterViewInit(): void {
    if (this.selected != null) {
      this._query = this.selected;
    }
  }

  get query(): string {
    return this._query;
  }

  set query(value: string) {
    this._query = value;
    this.onQueryChange.emit(this._query);
  }
}
