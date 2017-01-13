import { ConfigValue } from "../services/http/configValue.http.service";
import { PackageServerGroup } from "../services/http/packageservergroup.http.service";
import { PackageServer } from "../services/http/packageserver.http.service";
import { Package } from "../services/http/package.http.service";
import { Service } from "../services/http/service.http.service";
import { ConfigValieTreeNode } from "../configvalues/cv.overview.comp";
/**
 * Created by psigloch on 13.01.2017.
 */

export class Sorter {

  public static node(a: ConfigValieTreeNode, b: ConfigValieTreeNode) {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static configValue(a: ConfigValue, b: ConfigValue) {
    if (a.key < b.key) return -1;
    if (a.key > b.key) return 1;
    return 0;
  }

  public static group(a: PackageServerGroup, b: PackageServerGroup) {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static server(a: PackageServer, b: PackageServer) {
    if (a.description < b.description) return -1;
    if (a.description > b.description) return 1;
    return 0;
  }

  public static packages(a: Package, b: Package) {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }

  public static sortService(a: Service, b: Service) {
    if (a.name < b.name) return -1;
    if (a.name > b.name) return 1;
    return 0;
  }
}
