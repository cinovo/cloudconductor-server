import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';

import { AlertService } from '../util/alert/alert.service';
import { forbiddenNameValidator, Validator } from '../util/validator.util';
import { RepoMirror, RepoMirrorHttpService } from '../util/http/repomirror.http.service';
import { Repo, RepoHttpService } from '../util/http/repo.http.service';

/**
  * Copyright 2017 Cinovo AG<br>
  * <br>
  *
  * @author psigloch
  */
@Component({
  selector: 'mirror-edit',
  templateUrl: './mirror.edit.comp.html'
})
export class MirrorEdit implements OnInit {

  public mode = 'edit';
  public repoName: string;
  public mirrorForm: FormGroup;

  constructor(private repoHttp: RepoHttpService,
              private mirrorHttp: RepoMirrorHttpService,
              private route: ActivatedRoute,
              private alerts: AlertService,
              private router: Router,
              private fb: FormBuilder,
              private location: Location) {
    this.mirrorForm = fb.group({
      id: '',
      description: ['', [Validators.required, forbiddenNameValidator('new')]],
      path: ['', Validators.required],
      indexerType: ['NONE'],
      providerType: ['NONE'],
      repoName: '',
      basePath: '',
      bucketName: '',
      awsRegion: '',
      accessKeyId: '',
      secretKey: ''
    });
  };

  ngOnInit(): void {
    this.route.url.subscribe((value) => {
      if (value[value.length - 1].path === 'new') {
        this.mode = 'new'
      }
    });
    this.route.params.subscribe((params) => {
      this.repoName = params['repoName'];
      this.mirrorForm.controls.repoName.setValue(this.repoName);
      this.loadMirror(params['mirrorid'], this.repoName);
    });
  }

  get providerType() {
    return this.mirrorForm.controls.providerType.value;
  }

  save(formValue): void {
    const mirror: RepoMirror = {
      id: formValue.id,
      repo: formValue.repoName,
      description: formValue.description,
      path: formValue.path,
      indexerType: formValue.indexerType,
      providerType: formValue.providerType,
      basePath: formValue.basePath,
      bucketName: formValue.bucketName,
      awsRegion: formValue.awsRegion,
      accessKeyId: formValue.accessKeyId,
      secretKey: formValue.secretKey
    };

    mirror.path = mirror.path.trim();
    mirror.basePath = mirror.basePath.trim();

    this.doSave(mirror).subscribe(
      (result) => {
        this.alerts.success(`Successfully saved mirror '${mirror.description}'`);
        this.location.back();
      },
      (error) => this.alerts.danger('Failed to save the mirror')
    );
  }

  private doSave(mirror: RepoMirror): Observable<RepoMirror> {
    let call: Observable<RepoMirror>;
    if (Validator.idIsSet(this.mirrorForm.controls.id.value)) {
      call = this.mirrorHttp.editMirror(mirror);
    } else {
      call = this.mirrorHttp.newMirror(mirror);
    }

    return call
  }

  private loadMirror(mirrorid: any, repoName: string) {
    if (Validator.notEmpty(mirrorid) && +mirrorid > 0) {
      this.mirrorHttp.getMirror(mirrorid).subscribe((result) => {
        const formValue = {
          id: result.id,
          repoName: repoName,
          description: result.description,
          path: result.path,
          indexerType: result.indexerType,
          providerType: result.providerType,
          basePath: result.basePath || '',
          bucketName: result.bucketName || '',
          awsRegion: result.awsRegion || '',
          accessKeyId: result.accessKeyId || '',
          secretKey: result.secretKey || ''
        };
        this.mirrorForm.setValue(formValue);
      });
    }
  }
}
