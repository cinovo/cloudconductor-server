import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
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

export interface Template {
  name: string;
  description?: string;
  repos?: string[];
  versions?: { [packageName: string]: string };
  hosts?: HostIdentifier[];
  autoUpdate?: boolean;
  smoothUpdate?: boolean;
  group?: string;
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

@Injectable()
export class TemplateHttpService {

  private _basePathURL = 'api/template';

  constructor(private http: HttpClient) {
  }

  public getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(this._basePathURL).share();
  }

  public getTemplateNames(): Observable<string[]> {
    return this.getTemplates().map(templates => templates.map(template => template.name).sort());
  }

  public getTemplate(templateName: string): Observable<Template> {
    return this.http.get<Template>(`${this._basePathURL}/${templateName}`);
  }

  public existsTemplate(templateName: string): Observable<boolean> {
    return this.getTemplate(templateName).map((template: Template) => {
      return (template !== undefined);
    }).catch(() => {
      return Observable.of(false);
    });
  }

  public deleteTemplate(template: Template | SimpleTemplate): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${template.name}`);
  }

  public save(template: Template): Observable<boolean> {
    template['@class'] = 'de.cinovo.cloudconductor.api.model.Template';
    return this.http.put<boolean>(this._basePathURL, template);
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
    return this.http.get<Service[]>(`${this._basePathURL}/${templateName}/services`)
      .map(services => services.sort(Sorter.nameField));
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
    return this.http.get<SimpleTemplate[]>(`${this._basePathURL}/simple`).share();
  }

  public getDiff(templateAName: string, templateBName: string): Observable<PackageDiff[]> {
    return this.http.get<PackageDiff[]>(`${this._basePathURL}/packagediff/${templateAName}/${templateBName}`).share();
  }

  public getSimplePackageVersions(templateName: string): Observable<SimplePackageVersion[]> {
    return this.http.get<SimplePackageVersion[]>(`${this._basePathURL}/${templateName}/package/versions/simple`);
  }

}

