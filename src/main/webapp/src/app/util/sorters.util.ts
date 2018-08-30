import { ConfigValue } from './http/configValue.http.service';
import { Package, PackageVersion } from './http/package.http.service';
import { Service } from './http/service.http.service';
import { AdditionalLink } from './http/additionalLinks.http.service';
import { Repo } from './http/repo.http.service';
import { RepoMirror } from './http/repomirror.http.service';
import { Template } from './http/template.http.service';
import { Host } from './http/host.http.service';
import { ConfigFile } from './http/config-file.model';
import { SSHKey } from './http/sshkey.model';
import { TemplatePackageVersion } from '../template/template.package.comp';

/* tslint:disable:curly */

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export class Sorter {

  public static byField(a: any, b: any, field: string): number {
    if (a[field] < b[field]) return -1;
    if (a[field] > b[field]) return 1;
    return 0;
  }

  public static byFieldDesc(a: any, b: any, field: string): number {
    if (a[field] > b[field]) return -1;
    if (a[field] < b[field]) return 1;
    return 0;
  }

  public static nameField(a: any, b: any): number {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static groupField(a: any, b: any): number {
    if (!a.group && b.group) return -1;
    if (a.group && !b.group) return 1;
    if (a.group < b.group) return -1;
    if (a.group > b.group) return 1;
    return 0;
  }

  public static configValue(a: ConfigValue, b: ConfigValue): number {
    if (a.key < b.key) return -1;
    if (a.key > b.key) return 1;
    return 0;
  }

  public static repo(a: Repo, b: Repo): number {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static mirror(a: RepoMirror, b: RepoMirror): number {
    if (a.description < b.description) return -1;
    if (a.description > b.description) return 1;
    return 0;
  }

  public static packages(a: Package, b: Package): number {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static packageVersion(a: PackageVersion, b: PackageVersion): number {
    if (!a && b) return -1;
    if (a && !b) return 1;
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return Sorter.versionComp(a.version, b.version);
  }

  public static templatePackageVersion(a: TemplatePackageVersion, b: TemplatePackageVersion) {
    if (a.pkg < b.pkg) return -1;
    if (a.pkg > b.pkg) return 1;
    return 0;
  }

  public static stringReverse(a: string, b: string): number {
    if (a < b) return 1;
    if (a > b) return -1;
    return 0;
  }

  public static service(a: Service, b: Service): number {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static sshKey(a: SSHKey, b: SSHKey): number {
    if (a.owner < b.owner) return -1;
    if (a.owner > b.owner) return 1;
    return 0;
  }

  public static files(a: ConfigFile, b: ConfigFile) {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static host(a: Host, b: Host): number {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static template(a: Template, b: Template): number {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static links(a: AdditionalLink, b: AdditionalLink): number {
    if (a.label < b.label) return -1;
    if (a.label > b.label) return 1;
    return 0;
  }

  public static versionComp(a: string, b: string): number {
    let aNumbers: Array<any> = Sorter.versionToArray(a);
    let bNumbers: Array<any> = Sorter.versionToArray(b);

    const maxIndex = Math.max(aNumbers.length, bNumbers.length);

    for (let i = 0; i < maxIndex; i++) {
      let res: number;
      if (aNumbers[i] === undefined) res = -1;
      else if (bNumbers[i] === undefined) res = 1;
      else if (isNaN(aNumbers[i]) || isNaN(bNumbers[i])) {
        if (aNumbers[i].startsWith('SNAPSHOT') && !bNumbers[i].startsWith('SNAPSHOT')) res = -1;
        else if (!aNumbers[i].startsWith('SNAPSHOT') && bNumbers[i].startsWith('SNAPSHOT')) res = 1;
        else if (aNumbers[i] < bNumbers[i]) res = -1;
        else if (aNumbers[i] > bNumbers[i]) res = 1;
      } else {
        res = aNumbers[i] - bNumbers[i];
      }
      if (res !== 0) {
        return res;
      }
    }
    return 0;
  }

  private static versionToArray(version: string): Array<string> {
    let res = [];
    if (version) {
      for (let element of version.split('.')) {
        for (let sub of element.split('-')) {
          res.push(sub);
        }
      }
    }
    return res;
  }

}
