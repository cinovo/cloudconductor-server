import { Injectable } from "@angular/core";
import { RouterStateSnapshot, TitleStrategy } from "@angular/router";
import { Title } from "@angular/platform-browser";

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class CCTitleStrategy extends TitleStrategy {
  constructor(private readonly title: Title) {
    super();
  }

  override updateTitle(routerState: RouterStateSnapshot) {
    const title = this.buildTitle(routerState);
    if (title !== undefined) {
      this.title.setTitle(`${title} - CloudConductor`);
    }
  }
}
