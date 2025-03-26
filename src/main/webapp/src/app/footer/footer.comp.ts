import { Component, ViewEncapsulation } from '@angular/core';

@Component({
    selector: 'app-footer',
    templateUrl: './footer.comp.html',
    styleUrls: ['./footer.comp.scss'],
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class FooterComponent {

  public currentTime: number = Date.now();

}
