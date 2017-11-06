import { Directive, Input } from "@angular/core";


@Directive({
  selector: '[mwlConfirmationPopover]',
  host: {
    '(click)': 'onClick()'
  }
})
export class ConfirmationPopoverStubDirective {

  @Input() message: string;

  public onClick(): void {
    return;
  }

}
