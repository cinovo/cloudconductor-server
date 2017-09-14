import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';

import { Observable } from 'rxjs';
import { SSHKey } from '../util/http/sshkey.model';

export interface TemplateSelection {
  name: string,
  selected: boolean
}

@Component({
  templateUrl: './ssh.edit.comp.html',
  selector: 'app-ssh-edit'
})
export class SSHEditComponent implements OnInit {

  @Input() key: SSHKey;
  @Input() templateNames: Observable<string[]>;
  @Input() isCreate = false;

  @Output() onSave: EventEmitter<SSHKey> = new EventEmitter();
  @Output() onCancel: EventEmitter<String> = new EventEmitter();

  public templateSelection: TemplateSelection[];

  ngOnInit(): void {
    if (this.templateNames) {
      this.templateNames.subscribe((names) => {
        this.templateSelection = names.map((templateName) => {
          return { name: templateName, selected: this.isSelected(templateName) };
        });
      });
    }
  }

  private isSelected(templateName: string): boolean {
    return this.key.templates.indexOf(templateName) >= 0;
  }

  public save() {
    const keyToSave = this.key;
    keyToSave.templates = this.templateSelection.filter(ts => ts.selected).map(ts => ts.name);
    this.onSave.emit(keyToSave);
  }

  public cancel() {
    this.onCancel.emit('');
  }

}
