import { Directive, Input } from "@angular/core";


@Directive({
    selector: '[mwlConfirmationPopover]',
    host: {
        '(click)': 'onClick()'
    },
    standalone: false
})
export class ConfirmationPopoverStubDirective {

  @Input() message: string;

  public onClick(): void {
    return;
  }

}
