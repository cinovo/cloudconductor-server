import { Directive, Input } from "@angular/core";

@Directive({
  selector: '[routerLink]',
  host: {
    '(click)': 'onClick()'
  }
})
export class RouterLinkStubDirective {
  @Input('routerLink') linkParams: any;
  navigatedTo: any = null;

  public onClick() {
    this.navigatedTo = this.linkParams;
  }
}

@Directive({
  selector: '[queryParams]'
})
export class QueryParamStubDirective {
  @Input('queryParams') params: any;
}

export class RouterStub {
  public navigate(commands: any[]) {
    return Promise.resolve(true);
  }
}
