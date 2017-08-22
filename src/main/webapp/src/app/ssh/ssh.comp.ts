import { Component, OnInit } from '@angular/core';

import { SSHKey, SSHKeyHttpService } from '../util/http/sshKey.http.service';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Component({
  templateUrl: './ssh.comp.html'
})
export class SSHComponent implements OnInit {

  public sshKeys: SSHKey[] = [];

  constructor(private sshKeyHttp: SSHKeyHttpService) { }

  ngOnInit(): void {
    this.sshKeyHttp.getKeys().subscribe((keys) => this.sshKeys = keys);
  }

  public deleteKey(sshKey: SSHKey) {
    this.sshKeyHttp.deleteKey(sshKey.owner).subscribe(() => {

    });
  }

}
