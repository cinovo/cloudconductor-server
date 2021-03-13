import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Observable } from 'rxjs';

import { SSHKey } from '../util/http/sshkey.model';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './ssh.edit.comp.html',
  selector: 'app-ssh-edit'
})
export class SSHEditComponent implements OnInit {

  @Input() key: Observable<SSHKey>;
  @Input() templateNames: Observable<string[]>;
  @Input() isCreate = false;

  @Output() onSave: EventEmitter<SSHKey> = new EventEmitter();
  @Output() onCancel: EventEmitter<string> = new EventEmitter();

  public keyForm: FormGroup;

  constructor(private readonly fb: FormBuilder) {
    this.createForm();
  }

  private createForm(): void {
    this.keyForm = this.fb.group({
      owner: ['', [Validators.required, Validators.email]],
      username: ['root', Validators.required],
      key : ['', Validators.required],
      templates: [[]]
    });
  }

  ngOnInit(): void {
    if (this.key) {
      this.key.subscribe((k) => this.keyForm.setValue(k));
    }
  }

  public save(keyToSave: SSHKey): void {
    this.onSave.emit(keyToSave);
  }

  public cancel(): void {
    this.onCancel.emit('');
  }

}
