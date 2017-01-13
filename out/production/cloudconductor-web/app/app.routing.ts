/**
 * Created by thoprloph on 12.08.2016.
 */
import { Routes } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { PackageServersOverview } from "./packageservers/ps.overview.component";

export const APP_ROUTES: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},
  {path: 'hosts', component: HomeComponent},
  {path: 'files', component: HomeComponent},
  {path: 'settings', component: HomeComponent},
  {path: 'packageservers', component: PackageServersOverview},

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
