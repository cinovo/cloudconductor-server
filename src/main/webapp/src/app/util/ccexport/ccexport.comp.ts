import { Component, Inject, Input } from "@angular/core";
import { DOCUMENT } from "@angular/common";

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component({
  selector: 'cc-export',
  templateUrl: './ccexport.comp.html',
  styleUrls: ['./ccexport.comp.scss']
})
export class CCExport {

  @Input() fileName: string = CCExport.genFileName();
  @Input() title: string = 'Just copy the string below';

  @Input() export: string;

  constructor(@Inject(DOCUMENT) private readonly dom: Document) { }

  public copyToClipboard(exportCV: HTMLTextAreaElement) {
    exportCV.select();
    this.dom.execCommand("copy");
    exportCV.setSelectionRange(0, 0);
  }

  public download(exportElement: HTMLTextAreaElement) {
    let json = exportElement.value;
    let blob = new Blob([json], {type: 'application/json;charset=utf-8;'});
    let link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = this.fileName + ".json";
    link.click();
    link.remove();
  }

  private static genFileName(): string {
    let text = "";
    const possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (let i = 0; i < 10; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
  }

}
