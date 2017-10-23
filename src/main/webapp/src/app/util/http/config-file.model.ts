/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
export class FileForm {
  name: string;
  owner: string;
  group: string;
  fileMode: string;
  isTemplate: boolean;
  targetPath: string;
  dependentServices: string[];
  servicesReload: string[];
  templates: string[];
  fileContent: string;
  type: FileType

  constructor() {
    this.name = '';
    this.targetPath = '';
    this.owner = '';
    this.group = '';
    this.fileMode = '644';
    this.fileContent = '',
    this.isTemplate = false;
    this.type = FileType.File;
    this.dependentServices = [];
    this.templates = [];
    this.servicesReload = [];
  }

  public toConfigFile(): ConfigFile {
    const file = new ConfigFile();

    file.name = this.name;
    file.pkg = '';
    file.targetPath = this.targetPath;
    file.owner = this.owner;
    file.group = this.group;
    file.fileMode = this.fileMode;
    file.isDirectory = (this.type === FileType.Directory);
    file.isReloadable = (this.servicesReload.length > 0);
    file.isTemplate = this.isTemplate;
    file.dependentServices = this.dependentServices;
    file.templates = this.templates;

    return file
  }
}

export enum FileType {
  File = 'File',
  Directory = 'Directory'
}

export class ConfigFile {
  name: string;
  pkg: string;
  targetPath: string;
  owner: string;
  group: string;
  fileMode: string;
  isDirectory: boolean;
  isTemplate: boolean;
  isReloadable: boolean;
  checksum?: string;
  dependentServices: string[];
  templates: string[];

  constructor() {
    this.name = '';
    this.pkg = '';
    this.targetPath = '';
    this.owner = '';
    this.group = '';
    this.fileMode = '644';
    this.isTemplate = false;
    this.isReloadable = false;
    this.isDirectory = false;
    this.dependentServices = [];
    this.templates = [];
  }

  public toForm(): FileForm {
    const form = new FileForm();

    form.name = this.name;
    form.targetPath = this.targetPath;
    form.owner = this.owner;
    form.group = this.group;
    form.fileMode = this.fileMode;
    form.fileContent = '',
    form.isTemplate = this.isTemplate;
    form.type = (this.isDirectory) ? FileType.Directory : FileType.File;
    form.dependentServices = this.dependentServices;
    form.templates = this.templates;
    form.servicesReload = [];

    return form;
  }

}
