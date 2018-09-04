import { Component, Inject, Input, OnInit } from "@angular/core";
import { DOCUMENT } from "@angular/common";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cc-export',
  templateUrl: './ccexport.comp.html'
})
export class CCExport implements OnInit {


  @Input() fileName = CCExport.genFileName();
  @Input() title: string = 'Just copy the string below';

  @Input() export: string;

  constructor(@Inject(DOCUMENT) private dom: Document) {
  }

  ngOnInit(): void {

  }

  copyToClipboard(exportCV: HTMLInputElement) {
    exportCV.select();
    this.dom.execCommand("copy");
    exportCV.setSelectionRange(0, 0);
  }

  download(exportElement: HTMLInputElement) {
    let json = exportElement.value;
    let blob = new Blob([json], {type: 'application/json;charset=utf-8;'});
    let link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = this.fileName + ".json";
    link.click();
    link.remove();
  }

  static genFileName() {
    let text = "";
    const possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (let i = 0; i < 10; i++)
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    return text;
  }

}
