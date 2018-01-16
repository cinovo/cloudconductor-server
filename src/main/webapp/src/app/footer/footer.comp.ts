import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.comp.html',
  styleUrls: ['./footer.comp.scss'],
  encapsulation: ViewEncapsulation.None
})
export class FooterComponent {

  private currentTime: number = Date.now();




}
