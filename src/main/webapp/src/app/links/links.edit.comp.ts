import { Component, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';

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

export class LinksEdit implements AfterViewInit {

  public links: Array<AdditionalLink> = [];
  public newLink: AdditionalLink;

  private editLink: AdditionalLink;

  constructor(private linksHttp: AdditionalLinkHttpService,
              private alerts: AlertService) { };

  ngAfterViewInit(): void {
    this.loadLinks();
  }


  private loadLinks(): void {
    this.linksHttp.links.subscribe(
      (result) => this.links = result
    )
  }

  protected saveEditLink(): void {
    if (this.editLinkFieldValidation()) {
      return;
    }
    this.linksHttp.editLink(this.editLink).subscribe(
      () => {
        this.abortEditLink();
      },
      (error) => this.alerts.danger('The choosen label already exists. Please choose an unused label name.')
    );
  }

  protected saveNewLink(): void {
    if (this.newLinkFieldValidation()) {
      return;
    }
    this.linksHttp.newLink(this.newLink).subscribe(
      () => this.abortNewLink(),
      (error) => this.alerts.danger('The choosen label already exists. Please choose an unused label name.')
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
