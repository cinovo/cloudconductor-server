import { Component, Input} from '@angular/core';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cc-title',
  templateUrl: './cctitle.comp.html'
})
export class CCTitle {

  @Input() autorefresh = false;
  @Input() title: string;
  @Input() titleIcon: string;
  @Input() subtitle: string;

  constructor() { };

}
