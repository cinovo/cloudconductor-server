import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { BehaviorSubject, Subject } from 'rxjs/Rx';
import { Subscription } from 'rxjs/Subscription';

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

  constructor(private alertService: AlertService,
              private sshHttp: SSHKeyHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private templateHttp: TemplateHttpService) { }

  ngOnInit(): void {
    this.paraSub = this.route.paramMap.subscribe((paraMap) => {
      this.owner = paraMap.get('owner');
      this.keySub = this.sshHttp.getKey(this.owner).subscribe((key) => {
          this.key.next(key)
        },
        (err) => this.router.navigate(['/not-found', 'ssh', this.owner])
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
    this.sshHttp.updateKey(newSSHKey).subscribe(() => {
      this.alertService.success(`SSH Key of '${newSSHKey.owner}' was successfully updated!`);
      this.router.navigate(['/ssh']);
    },
    (err) => {
      this.alertService.danger(`Error updating SSH key of '${newSSHKey.owner}'!`);
      console.error(err);
    });
  }

  public cancelEdit() {
    this.router.navigate(['/ssh']);
  }
}
