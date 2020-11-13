import { Component, EventEmitter, Input, Output, OnDestroy, OnInit } from '@angular/core';

import { Observable ,  Subscription } from 'rxjs';

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
export class HomeServiceStatusComponent implements OnInit, OnDestroy {

  @Input() servicesObs: Observable<Service[]>;
  @Output() onServiceClicked: EventEmitter<string> = new EventEmitter<string>();

  public lastUpdate: number;
  private servicesSub: Subscription;

  constructor() { }

  ngOnInit(): void {
    this.servicesObs.subscribe(() => {
      this.lastUpdate = new Date().getTime();
    });
  }

  ngOnDestroy(): void {
    if (this.servicesSub) {
      this.servicesSub.unsubscribe();
    }
  }

}
