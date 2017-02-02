/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export enum Mode {
  EDIT,
  NEW
}

export enum ServiceState {
  STARTING,
  STARTED,
  IN_SERVICE,
  STOPPING,
  STOPPED,
  RESTARTING_STOPPING,
  RESTARTING_STARTING
}
