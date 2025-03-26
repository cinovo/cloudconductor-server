import {AfterViewInit, Component, inject, Input} from '@angular/core';

import { Observable } from 'rxjs';

import { Host, HostHttpService } from '../util/http/host.http.service';
import { ServiceState } from '../util/enums.util';
import { Sorter } from '../util/sorters.util';

interface ServiceStateElement {
  name: string;
  state: ServiceState;
  autostart?: boolean;
  selected?: boolean;
}

type ServiceActionType = 'start' | 'stop' | 'restart';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
    selector: 'host-services',
    templateUrl: './host.service.comp.html',
    standalone: false
})
export class HostServices implements AfterViewInit {

  @Input() obsHost: Observable<Host>;

  public services = [] as ServiceStateElement[];
  public host = {name: '', template: '', uuid: ''} as Host;

  private _allSelected = false;

  private readonly hostHTTP = inject(HostHttpService);

  ngAfterViewInit(): void {
    this.obsHost.subscribe(
      (result) => {
        this.loadServices(result);
        this.host = result;
      }
    );
  }

  private loadServices(host: Host): void {
    let old = this.services;
    this.services = [];
    if (host && host.services && Object.keys(host.services).length > 0) {
      for (let index of Object.keys(host.services)) {
        let selected = this.allSelected;
        if (!selected && old && old.length > 0) {
          for (let oldElement of old) {
            if (oldElement.name === index) {
              selected = oldElement.selected;
            }
          }
        }
        this.services.push({name: index, state: host.services[index], autostart: false, selected: selected})
      }
      this.services.sort(Sorter.nameField)
    }
  }

  public start(service: ServiceStateElement) {
    if (service) {
      this.hostHTTP.startService(this.host.uuid, service.name).subscribe(
        () => {},
        (err) => console.error(err)
      );
    }
  }

  public stop(service: ServiceStateElement) {
    if (service) {
      this.hostHTTP.stopService(this.host.uuid, service.name).subscribe(
        () => {},
        (err) => console.error(err)
      );
    }
  }

  public restart(service: ServiceStateElement) {
    if (service) {
      this.hostHTTP.restartService(this.host.uuid, service.name).subscribe(
        () => {},
        (err) => console.error(err)
      );
    }
  }

  public handleSelected(type: ServiceActionType) {
    const serviceNames: string[] = this.services.filter(s => s.selected).map(s => s.name);
    this.handleServices(type, serviceNames);
    this.allSelected = false;
    return;
  }

  private handleServices(type: ServiceActionType, serviceNames: string[]) {
    if (serviceNames && serviceNames.length > 0) {
      let currentServiceName = serviceNames.pop();
      this.httpServiceCall(type, currentServiceName,
        () => {
          this.handleServices(type, serviceNames);
        }, () => {
          this.handleServices(type, serviceNames);
        }
      );
    }
  }

  private httpServiceCall(type: ServiceActionType, serviceName: string, successCallback: () => void, errorCallBack?: (err) => void): void {
    switch (type) {
      case 'start':
        this.hostHTTP.startService(this.host.uuid, serviceName).subscribe(successCallback, errorCallBack);
        break;
      case 'stop':
        this.hostHTTP.stopService(this.host.uuid, serviceName).subscribe(successCallback, errorCallBack);
        break;
      case 'restart':
        this.hostHTTP.restartService(this.host.uuid, serviceName).subscribe(successCallback, errorCallBack);
        break;
    }
  }

  public isServiceStarted(service: ServiceStateElement, includeTransient = false): boolean {
    if (service.state.toString() === ServiceState[ServiceState.STARTED] || service.state.toString() === ServiceState[ServiceState.IN_SERVICE]) {
      return true;
    }
    return includeTransient && (service.state.toString() === ServiceState[ServiceState.STARTING] || this.isServiceRestarting(service));
  }

  public isServiceTransient(service: ServiceStateElement): boolean {
    return !this.isServiceStarted(service) && !this.isServiceStopped(service)
  }

  public isServiceStopped(service: ServiceStateElement, includeTransient = false): boolean {
    if (service.state.toString() === ServiceState[ServiceState.STOPPED]) {
      return true;
    }
    return includeTransient && (service.state.toString() === ServiceState[ServiceState.STOPPING] || this.isServiceRestarting(service));
  }

  public isServiceRestarting(service: ServiceStateElement): boolean {
    return (service.state.toString() === ServiceState[ServiceState.RESTARTING_STARTING] ||
      service.state.toString() === ServiceState[ServiceState.RESTARTING_STOPPING]);
  }

  get allSelected(): boolean {
    return this._allSelected;
  }

  set allSelected(value: boolean) {
    this._allSelected = value;
    for (let pv of this.services) {
      pv.selected = this._allSelected;
    }
  }

  public isSelected(type: ServiceActionType): boolean {
    for (let service of this.services) {
      if (service.selected) {
        switch (type) {
          case 'start':
            if (!this.isServiceStarted(service, true)) {
              return true;
            }
            break;
          case 'stop':
            if (!this.isServiceStopped(service, true)) {
              return true;
            }
            break;
          case 'restart':
            if (!this.isServiceStopped(service, true)) {
              return true;
            }
            break;
        }
      }
    }
    return false;
  }
}
