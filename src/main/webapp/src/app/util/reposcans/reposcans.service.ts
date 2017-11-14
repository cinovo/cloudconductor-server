import { Injectable } from '@angular/core';
import { Repo } from '../http/repo.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Injectable()
export class RepoScansService {

  private scanInterval = 60 * 1000;

  public isDue(repo: Repo): boolean {
    const diff = new Date().getTime() - repo.lastIndex;
    return diff > this.scanInterval;
  }

  public isPastDue(repo: Repo): boolean {
    const diff = new Date().getTime() - repo.lastIndex;
    return diff > 2 * this.scanInterval;
  }

}
