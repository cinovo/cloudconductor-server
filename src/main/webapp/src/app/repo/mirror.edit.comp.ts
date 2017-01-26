import { Component, AfterViewInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertService } from "../services/alert/alert.service";
import { Observable } from "rxjs";
import { Validator } from "../util/validator.util";
import { RepoMirror, RepoMirrorHttpService } from "../services/http/repomirror.http.service";
import { Repo, RepoHttpService } from "../services/http/repo.http.service";

/**
  * Copyright 2017 Cinovo AG<br>
  * <br>
  *
  * @author psigloch
  */
@Component({
  moduleId: module.id,
  selector: 'mirror-edit',
  templateUrl: 'html/mirror.edit.comp.html'
})
export class MirrorEdit implements AfterViewInit {

  private mode: string = "edit";
  private returnToOverview: boolean = false;
  private mirror: RepoMirror = {
    path: "",
    basePath: "",
    description: "",
    indexerType: "NONE",
    providerType: "NONE",
    repo: ""
  };

  private repo: Repo = {name: "", mirrors: [], primaryMirror: 0};

  constructor(private repoHttp: RepoHttpService, private mirrorHttp: RepoMirrorHttpService,
              private route: ActivatedRoute, private alerts: AlertService, private router: Router) {
  };

  ngAfterViewInit(): void {
    this.route.url.subscribe((value) => {
        if (value[value.length - 1].path == 'new') {
          this.mode = 'new'
        }
      }
    );
    this.route.params.subscribe((params) => {
      this.loadRepo(params['repoName']);
      this.mirror.repo = params['repoName'];
      this.loadMirror(params['mirrorid']);
    });

    this.route.queryParams.subscribe((params) => {
      if (params['ret'] && params['ret'] == "ov") {
        this.returnToOverview = true;
      }
    });
  }


  save(): void {
    if (this.fieldValidation()) {
      return;
    }
    let call: Observable<RepoMirror>;
    if (Validator.idIsSet(this.mirror.id)) {
      call = this.mirrorHttp.editMirror(this.mirror);
    } else {
      call = this.mirrorHttp.newMirror(this.mirror);
    }
    call.subscribe(
      (result) => {
        this.mirror = result;
        if (this.returnToOverview) {
          this.router.navigate(["repo"]);
        } else {
          this.router.navigate(["repo", this.repo.name])
        }
      },
      (error) => {
        this.alerts.danger("Failed to save the mirror");
        console.log(error);
      }
    );
  }

  private fieldValidation() {
    let error = false;
    if (!this.isDescriptionValid()) {
      this.alerts.danger("Please insert a description.");
      error = true;
    }
    if (!this.isPathValid()) {
      this.alerts.danger("Please insert a path.");
      error = true;
    }
    return error;
  }

  private isDescriptionValid(): boolean {
    return Validator.notEmpty(this.mirror.description);
  }

  private isPathValid(): boolean {
    return Validator.notEmpty(this.mirror.path);
  }

  private loadRepo(repoName: string): void {
    if (Validator.notEmpty(repoName)) {
      this.repoHttp.getRepo(repoName).subscribe((result) => {
        this.repo = result;
      });
    }
  }

  private loadMirror(mirrorid: any) {
    if (Validator.notEmpty(mirrorid) && +mirrorid > 0) {
      this.mirrorHttp.getMirror(mirrorid).subscribe((result) => {
        this.mirror = result;
      });
    }
  }
}
