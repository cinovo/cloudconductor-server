import { Component, Input } from '@angular/core';

import { Observable } from 'rxjs';

import { Stats } from '../util/http/stats.http.service';

@Component({
    selector: 'home-stats',
    templateUrl: './home.stats.comp.html',
    standalone: false
})
export class HomeStatsComponent {

  @Input() statsObs: Observable<Stats>;

}
