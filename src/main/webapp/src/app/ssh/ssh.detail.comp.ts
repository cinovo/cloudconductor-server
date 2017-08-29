import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../util/alert/alert.service';
import { SSHKey, SSHKeyHttpService } from '../util/http/sshKey.http.service';
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

  public loaded = false;
  public owner: string;
  public key: SSHKey;
  public templateNames: Observable<string[]>;

  private paraSub: Subscription;

  constructor(private alertService: AlertService,
              private sshHttp: SSHKeyHttpService,
              private route: ActivatedRoute,
              private router: Router,
              private templateHttp: TemplateHttpService) { }

  ngOnInit(): void {
    this.paraSub = this.route.paramMap.subscribe((paraMap) => {
      this.owner = paraMap.get('owner');
      this.sshHttp.getKey(this.owner).subscribe((key) => {
        this.key = key;
        this.loaded = true;
      },
      (err) => {
        this.loaded = true;
      });
    });

    this.templateNames = this.templateHttp.getTemplateNames();
  }

  ngOnDestroy(): void {
    if (this.paraSub) {
      this.paraSub.unsubscribe();
    }
  }

  public saveKey(newSSHKey: SSHKey) {
    this.sshHttp.updateKey(this.key).subscribe(() => {
      this.alertService.success(`SSH Key of ${this.key.owner} was successfully updated!`);
      this.router.navigate(['/ssh']);
    },
    (err) => {
      this.alertService.danger(`Error updating SSH key of ${this.key.owner}!`);
      console.error(err);
    });
  }

  public cancelEdit() {
    this.router.navigate(['/ssh']);
  }
}
