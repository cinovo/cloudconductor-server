import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { Service } from '../util/http/service.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'home-servicestatus',
  templateUrl: './home.servicestatus.comp.html'
})
export class HomeServiceStatusComponent {

  @Input() servicesObs: Observable<Service[]>;
  @Output() onServiceClicked: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }

  public serviceClicked(service: Service): void {
    this.onServiceClicked.emit(service.name);
  }

}
