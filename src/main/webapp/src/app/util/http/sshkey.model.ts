/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export interface SSHKey {
  owner: string,
  username: string,
  key: string,
  lastChanged?: Date
  templates: string[]
}
