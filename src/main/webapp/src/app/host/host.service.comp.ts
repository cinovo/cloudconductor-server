import { AfterViewInit, Component, Input } from '@angular/core';

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
  templateUrl: './host.service.comp.html'
})
export class HostServices implements AfterViewInit {

  @Input() obsHost: Observable<Host>;

  public services: Array<ServiceStateElement> = [];
  public host: Host = {name: '', template: '', uuid: ''};

  private _allSelected = false;

  constructor(private readonly hostHTTP: HostHttpService) {  }

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

  public handleService(type: ServiceActionType, service: ServiceStateElement): void {
    if (service) {
      this.httpServiceCall(type, service.name,
        () => {
        },
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

  public isServiceStarted(service: ServiceStateElement, includeTrannsient = false): boolean {
    let ret: boolean = (service.state.toString() === ServiceState[ServiceState.STARTED] ||
      service.state.toString() === ServiceState[ServiceState.IN_SERVICE]);
    if (includeTrannsient && !ret) {
      ret = service.state.toString() === ServiceState[ServiceState.STARTING] || this.isServiceRestarting(service);
    }
    return ret;
  }

  public isServiceTransient(service: ServiceStateElement): boolean {
    return !this.isServiceStarted(service) && !this.isServiceStoped(service)
  }

  public isServiceStoped(service: ServiceStateElement, includeTrannsient = false): boolean {
    let ret: boolean = service.state.toString() === ServiceState[ServiceState.STOPPED];
    if (includeTrannsient && !ret) {
      ret = service.state.toString() === ServiceState[ServiceState.STOPPING] || this.isServiceRestarting(service);
    }
    return ret;
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
            if (!this.isServiceStoped(service, true)) {
              return true;
            }
            break;
          case 'restart':
            if (!this.isServiceStoped(service, true)) {
              return true;
            }
            break;
        }
      }
    }
    return false;
  }
}
