import { Component, ViewEncapsulation } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  moduleId: module.id,
  selector: 'top-navbar',
  styleUrls: ['css/nav.component.css'],
  templateUrl: 'html/topNav.component.html',
  encapsulation: ViewEncapsulation.None
})
export class TopNavComponent {

  constructor(private router: Router) {

  };

}
