import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs/Subscription';

import { AdditionalLinkHttpService, AdditionalLink } from '../util/http/additionalLinks.http.service';
import { Validator } from '../util/validator.util';
import { AlertService } from '../util/alert/alert.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'links-edit',
  templateUrl: './links.edit.comp.html'
})
export class LinksEdit implements OnInit, OnDestroy {

  public links: AdditionalLink[] = [];
  public newLink: AdditionalLink;
  public editLink: AdditionalLink;

  private _linksSub: Subscription;

  constructor(private linksHttp: AdditionalLinkHttpService,
              private alerts: AlertService) { };

  ngOnInit(): void {
    this.loadLinks();
  }

  ngOnDestroy(): void {
    if (this._linksSub) {
      this._linksSub.unsubscribe();
    }
  }

  private loadLinks(): void {
    this._linksSub = this.linksHttp.links.subscribe(
      (result) => this.links = result,
      (err) => console.error(err)
    )
  }

  protected saveEditLink(): void {
    if (this.editLinkFieldValidation()) {
      return;
    }
    this.linksHttp.editLink(this.editLink).subscribe(
      () => this.abortEditLink(),
      (error) => this.alerts.danger('Error editing link!')
    );
  }

  protected saveNewLink(): void {
    if (this.newLinkFieldValidation()) {
      return;
    }
    this.linksHttp.newLink(this.newLink).subscribe(
      () => this.abortNewLink(),
      (error) => this.alerts.danger('Error creating link: The choosen label already exists. Please choose another label.')
    );
  }

  protected deleteLink(link: AdditionalLink): void {
    this.linksHttp.deleteLink(link.id);
  }

  protected goToAddLink(): void {
    this.abortEditLink();
    this.newLink = {label: '', url: ''};
  }

  protected goToEditLink(link: AdditionalLink) {
    this.abortNewLink();
    this.editLink = link;
  }

  private abortEditLink(): void {
    this.editLink = null;
  }

  private abortNewLink(): void {
    this.newLink = null;
  }

  private editLinkFieldValidation(): boolean {
    let error = false;
    if (!this.isEditLinkLabelValid()) {
      this.alerts.danger('Please insert a link label.');
      error = true;
    }
    if (!this.isEditLinkUrlValid()) {
      this.alerts.danger('Please insert a link url.');
      error = true;
    }
    return error;
  }

  private newLinkFieldValidation(): boolean {
    let error = false;
    if (!this.isNewLinkLabelValid()) {
      this.alerts.danger('Please insert a link label.');
      error = true;
    }
    if (!this.isNewLinkUrlValid()) {
      this.alerts.danger('Please insert a link url.');
      error = true;
    }
    return error;
  }

  private isEditLinkLabelValid(): boolean {
    return Validator.notEmpty(this.editLink.label);
  }

  private isEditLinkUrlValid(): boolean {
    return Validator.notEmpty(this.editLink.url);
  }

  private isNewLinkLabelValid(): boolean {
    return Validator.notEmpty(this.newLink.label);
  }

  private isNewLinkUrlValid(): boolean {
    return Validator.notEmpty(this.newLink.url);
  }
}
