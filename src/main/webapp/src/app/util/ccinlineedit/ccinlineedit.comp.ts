import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cc-inlineedit',
  templateUrl: './ccinlineedit.comp.html'
})
export class CCInlineEdit implements OnInit {

  @Input() value: string;
  @Output() onSave: EventEmitter<any> = new EventEmitter();
  @Output() onEditDone: EventEmitter<any> = new EventEmitter();

  get editMode() {
    return this.editing;
  }

  @Input()
  set editMode(val: boolean) {
    if (val == true && val != this.editing && !this.editing) {
      this.edit();
    }
  }

  private editing: boolean = false;
  private oldVal: string;

  // Is Component in edit mode?

  constructor() {
  }

  ngOnInit(): void {

  }

  edit() {
    this.oldVal = this.value;
    this.editing = true;
  }

  save() {
    this.editing = false;
    this.onSave.emit(this.value);
    this.onEditDone.emit();
  }

  abort() {
    this.editing = false;
    this.value = this.oldVal;
    this.onEditDone.emit();
  }
}
