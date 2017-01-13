/**
 * Created by thoprloph on 12.08.2016.
 */
import { Routes } from "@angular/router";
import { HomeComponent } from "./home/home.comp";
import { PackageServersOverview } from "./packageservers/ps.overview.comp";
import { PackageServerGroupEdit } from "./packageservers/psg.edit.comp";
import { PackageServerEdit } from "./packageservers/ps.edit.comp";
import { ConfigValueOverview } from "./configvalues/cv.overview.comp";
import { ConfigValueEdit } from "./configvalues/cs.edit.comp";
import { ServiceOverview } from "./service/service.overview.comp";
import { ServiceDetail } from "./service/service.detail.comp";

export const APP_ROUTES: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},

  {path: 'hosts', component: HomeComponent},

  {path: 'files', component: HomeComponent},

  {path: 'settings', component: HomeComponent},

  {path: 'packageservers', component: PackageServersOverview},
  {path: 'packageservers/new', component: PackageServerGroupEdit},
  {path: 'packageservers/:id', component: PackageServerGroupEdit},
  {path: 'packageservers/:id/server/new', component: PackageServerEdit},
  {path: 'packageservers/:id/server/:serverid', component: PackageServerEdit},

  {path: 'config/:template', component: ConfigValueOverview},
  {path: 'config/:template/:service/new', component: ConfigValueEdit},
  {path: 'config/:template/:service/:key', component: ConfigValueEdit},

  {path: 'services', component: ServiceOverview},
  {path: 'services/:serviceName', component: ServiceDetail},
  {path: 'services/new', component: ServiceDetail},


  {path: '**', redirectTo: '/home', pathMatch: 'full'},
];

export const ROUTED_COMPONENTS = () => {
  let res = []
  for (let route of APP_ROUTES) {
    if (route.component) {
      res.push(route.component);
    }
  }
  return res;
}
