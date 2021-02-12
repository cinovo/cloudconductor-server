import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  selector: 'not-found.comp',
  templateUrl: './not-found.comp.html'
})
export class NotFoundComponent implements OnInit {

  public type = 'Page';
  public name = '';

  constructor(private readonly route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((paraMap) => {
      const type = paraMap.get('type');
      if (type.length > 0) {
        this.type = type.replace(/\w\S*/g, (t) => t.charAt(0).toUpperCase() + t.substr(1).toLowerCase());
      }
      this.name = paraMap.get('name');
    });
  }

}
