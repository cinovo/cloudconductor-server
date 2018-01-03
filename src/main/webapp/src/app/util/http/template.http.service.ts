import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Template {
  name: string;
  description?: string;
  repos?: string[];
  versions?: {[packageName: string]: string};
  hosts?: string[];
  autoUpdate?: boolean;
  smoothUpdate?: boolean;
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

@Injectable()
export class TemplateHttpService {

  private _basePathURL = 'api/template';

  constructor(private http: HttpClient) { }

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

  public deleteTemplate(template: Template): Observable<boolean> {
    return this.http.delete<boolean>(`${this._basePathURL}/${template.name}`);
  }

  public save(template: Template): Observable<boolean> {
    template['@class'] = 'de.cinovo.cloudconductor.api.model.Template';
    return this.http.put<boolean>(this._basePathURL, template);
  }

  public updatePackage(template: Template, packageName: string): Observable<Template> {
    return this.http.put<Template>(`${this._basePathURL}/${template.name}/package/${packageName}`, '');
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

}

