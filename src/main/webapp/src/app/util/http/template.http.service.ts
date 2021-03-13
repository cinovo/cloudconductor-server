import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { of as observableOf, Observable } from 'rxjs';
import { catchError, map, share } from 'rxjs/operators';

import { Service } from './service.http.service';
import { Sorter } from '../sorters.util';
import { SimplePackageVersion } from "./package.http.service";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface HostIdentifier {
  name: string;
  uuid: string;
}

export interface SimpleTemplate {
  name: string;
  hostCount?: number;
  packageCount?: number;
  repos?: string[];
  group?: string;
}

export type UpdateRange ='all' | 'major' | 'minor' | 'patch'

export interface Template {
  name: string;
  description?: string;
  repos?: string[];
  versions?: { [packageName: string]: string };
  hosts?: HostIdentifier[];
  autoUpdate?: boolean;
  smoothUpdate?: boolean;
  group?: string;
  updateRange?: UpdateRange;
}

export interface PackageDiff {
  name: string;
  version: string;
}

export interface AgentOption {
  aliveTimer?: number;
  aliveTimerUnit?: string;

  doSshKeys?: string;
  sshKeysTimer?: number;
  sshKeysTimerUnit?: string;

  doPackageManagement?: string;
  packageManagementTimer?: number;
  packageManagementTimerUnit?: string;

  doFileManagement?: string;
  fileManagementTimer?: number;
  fileManagementTimerUnit?: string;

  templateName?: string;
}

export type ServiceState = 'STARTED' | 'STOPPED';

export interface ServiceDefaultState {
  template: string;
  service: string;
  state: ServiceState;
}

export interface PackageVersionMultiMap {
  [packageName: string]: string[];
}

export interface TemplateUpdates {
  available: PackageVersionMultiMap;
  inRange: PackageVersionMultiMap;
}

@Injectable()
export class TemplateHttpService {

  private readonly _basePathURL = 'api/template';

  constructor(private readonly http: HttpClient) { }

  public getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(this._basePathURL).pipe(share());
  }

  public getTemplateNames(): Observable<string[]> {
    return this.getTemplates().pipe(map(templates => templates.map(template => template.name).sort()));
  }

  public getTemplate(templateName: string): Observable<Template> {
    return this.http.get<Template>(`${this._basePathURL}/${templateName}`);
  }

  public existsTemplate(templateName: string): Observable<boolean> {
    return this.getTemplate(templateName).pipe(
      map((template: Template) => (template !== undefined)), //
      catchError(() => observableOf(false)), //
    );
  }

  public deleteTemplate(template: Template | SimpleTemplate): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${template.name}`);
  }

  public save(template: Template): Observable<boolean> {
    return this.http.put<boolean>(this._basePathURL,{'@class': 'de.cinovo.cloudconductor.api.model.Template', ...template});
  }

  public updatePackage(template: Template, packageName: string): Observable<Template> {
    return this.http.put<Template>(`${this._basePathURL}/${template.name}/package/${packageName}`, {});
  }

  public deletePackage(template: Template, packageName: string): Observable<Template> {
    return this.http.delete<Template>(`${this._basePathURL}/${template.name}/package/${packageName}`);
  }

  public loadAgentOptions(templateName: string): Observable<AgentOption> {
    return this.http.get<AgentOption>(`${this._basePathURL}/${templateName}/agentoption`);
  }

  public saveAgentOptions(agentOption: AgentOption): Observable<AgentOption> {
    agentOption['@class'] = 'de.cinovo.cloudconductor.api.model.AgentOption';
    return this.http.put<AgentOption>(`${this._basePathURL}/${agentOption.templateName}/agentoption`, agentOption);
  }

  public getServicesForTemplate(templateName: string): Observable<Service[]> {
    return this.http.get<Service[]>(`${this._basePathURL}/${templateName}/services`).pipe(
      map(services => services.sort(Sorter.nameField)));
  }

  public getServiceDefaultStates(templateName: string): Observable<ServiceDefaultState[]> {
    return this.http.get<ServiceDefaultState[]>(`${this._basePathURL}/${templateName}/servicedefaultstate`);
  }

  public getServiceDefaultState(templateName: string, serviceName: string): Observable<ServiceDefaultState> {
    return this.http.get<ServiceDefaultState>(`${this._basePathURL}/${templateName}/servicedefaultstate/${serviceName}`);
  }

  public saveServiceDefaultState(templateName: string, serviceName: string, serviceDefaultState: ServiceState) {
    const newServiceDefaultState = {template: templateName, service: serviceName, defaultState: serviceDefaultState};
    newServiceDefaultState['@class'] = 'de.cinovo.cloudconductor.api.model.ServiceDefaultState';
    return this.http.put<ServiceDefaultState>(`${this._basePathURL}/${templateName}/servicedefaultstate/${serviceName}`, newServiceDefaultState);
  }

  public getSimpleTemplates(): Observable<SimpleTemplate[]> {
    return this.http.get<SimpleTemplate[]>(`${this._basePathURL}/simple`).pipe(share());
  }

  public getDiff(templateAName: string, templateBName: string): Observable<PackageDiff[]> {
    return this.http.get<PackageDiff[]>(`${this._basePathURL}/packagediff/${templateAName}/${templateBName}`).pipe(share());
  }

  public getSimplePackageVersions(templateName: string): Observable<SimplePackageVersion[]> {
    return this.http.get<SimplePackageVersion[]>(`${this._basePathURL}/${templateName}/package/versions/simple`);
  }

  public replacePackageVersionsForTemplate(templateName: string, packageVersions: SimplePackageVersion[]): Observable<Template> {
    return this.http.put<Template>(`${this._basePathURL}/${templateName}/package/versions`, packageVersions);
  }

  public getUpdates(templateName: string): Observable<TemplateUpdates> {
    return this.http.get<TemplateUpdates>(`${this._basePathURL}/${templateName}/updates`)
  }
}



