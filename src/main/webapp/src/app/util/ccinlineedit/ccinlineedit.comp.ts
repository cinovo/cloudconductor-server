import { Component, EventEmitter, Input, Output } from "@angular/core";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
    selector: 'cc-inlineedit',
    templateUrl: './ccinlineedit.comp.html',
    standalone: false
})
export class CCInlineEdit {

  @Input() value: string;
  @Input() options: string[] = [];
  @Output() onSave: EventEmitter<string> = new EventEmitter();
  @Output() onEditDone: EventEmitter<void> = new EventEmitter();

  private editing = false;
  private oldVal: string;

  get editMode(): boolean {
    return this.editing;
  }

  @Input()
  set editMode(value: boolean) {
    if (value && !this.editing) {
      this.edit();
    }
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
