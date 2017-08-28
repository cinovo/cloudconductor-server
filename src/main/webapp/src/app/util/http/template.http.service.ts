import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs';

import { HTTPService } from './abstract.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export interface Template {
  name: string;
  description?: string;
  repos?: Array<string>;
  versions?: {[packageName: string]: string};
  hosts?: Array<String>;
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
export class TemplateHttpService extends HTTPService {

  constructor(protected http: Http) {
    super(http);
    this.basePathURL = 'template/';
  }

  public getTemplates(): Observable<Array<Template>> {
    return this._get('');
  }

  public getTemplate(templateName: string): Observable<Template> {
    return this._get(templateName);
  }

  public deleteTemplate(template: Template): Observable<boolean> {
    return this._delete(template.name);
  }

  public save(template: Template): Observable<boolean> {
    template['@class'] = 'de.cinovo.cloudconductor.api.model.Template';
    return this._put('', template);
  }

  public updatePackage(template: Template, packageName: string): Observable<Template> {
    return this._put(template.name + '/package/' + packageName, '');
  }

  public deletePackage(template: Template, packageName: string): Observable<Template> {
    return this._delete(template.name + '/package/' + packageName);
  }

  public loadAgentOptions(templateName: string): Observable<AgentOption> {
    return this._get(templateName + '/agentoption');
  }

  public saveAgentOptions(agentOption: AgentOption): Observable<AgentOption> {
    agentOption['@class'] = 'de.cinovo.cloudconductor.api.model.AgentOption';
    return this._put(agentOption.templateName + '/agentoption', agentOption);
  }
}

