import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';

import { AlertService } from '../util/alert/alert.service';
import { SSHKey } from '../util/http/sshkey.model';
import { SSHKeyHttpService } from '../util/http/sshKey.http.service';
import { TemplateHttpService } from '../util/http/template.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './ssh.detail.comp.html'
})
export class SSHDetailComponent implements OnInit, OnDestroy {

  public owner: string;
  public key: Subject<SSHKey> = new BehaviorSubject<SSHKey>({ owner: '', username: '', key: '', templates: []});
  public templateNames: Observable<string[]>;

  private paraSub: Subscription;
  private keySub: Subscription;

  constructor(private readonly alertService: AlertService,
              private readonly sshHttp: SSHKeyHttpService,
              private readonly route: ActivatedRoute,
              private readonly router: Router,
              private readonly templateHttp: TemplateHttpService) { }

  ngOnInit(): void {
    this.paraSub = this.route.paramMap.subscribe((paraMap) => {
      this.owner = paraMap.get('owner');
      this.keySub = this.sshHttp.getKey(this.owner).subscribe((key) => {
          this.key.next(key)
        },
        (_) => this.router.navigate(['/not-found', 'ssh', this.owner])
      );
    });

    this.templateNames = this.templateHttp.getTemplateNames();
  }

  ngOnDestroy(): void {
    if (this.paraSub) {
      this.paraSub.unsubscribe();
    }

    if (this.keySub) {
      this.keySub.unsubscribe();
    }
  }

  public saveKey(newSSHKey: SSHKey) {
    const sshKeyToSave: SSHKey = { owner: newSSHKey.owner.trim(), ...newSSHKey};
    this.sshHttp.updateKey(sshKeyToSave).subscribe(() => {
      this.alertService.success(`SSH Key of '${sshKeyToSave.owner}' was successfully updated!`);
      // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/ssh']);
    },
    (err) => {
      this.alertService.danger(`Error updating SSH key of '${sshKeyToSave.owner}'!`);
      console.error(err);
    });
  }

  public cancelEdit(): Promise<boolean> {
    return this.router.navigate(['/ssh']);
  }
}
