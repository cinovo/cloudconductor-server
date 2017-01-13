import { Component, ViewEncapsulation } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  moduleId: module.id,
  selector: 'top-navbar',
  styleUrls: ['css/topNav.comp.css'],
  templateUrl: 'html/topNav.comp.html',
  encapsulation: ViewEncapsulation.None
})
export class TopNavComponent {

  constructor(private router: Router) {

  };

}
