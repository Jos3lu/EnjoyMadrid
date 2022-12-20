import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadChildren: () => import('./pages/index/index.module').then( m => m.IndexPageModule)
  },
  {
    path: 'create-route',
    loadChildren: () => import('./pages/create-route/create-route.module').then( m => m.CreateRoutePageModule)
  },
  {
    path: 'find-places',
    loadChildren: () => import('./pages/find-places/find-places.module').then( m => m.FindPlacesPageModule)
  },
  {
    path: 'update-user',
    loadChildren: () => import('./pages/update-user/update-user.module').then( m => m.UpdateUserPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'sign',
    loadChildren: () => import('./pages/sign/sign.module').then( m => m.LoginPageModule)
  },
  {
    path: 'display-route',
    loadChildren: () => import('./pages/display-route/display-route.module').then( m => m.DisplayRoutePageModule)
  },
  {
    path: 'store-places',
    loadChildren: () => import('./pages/store-places/store-places.module').then( m => m.StorePlacesPageModule)
  },
  {
    path: '**',
    loadChildren: () => import('./pages/page-not-found/page-not-found.module').then( m => m.PageNotFoundPageModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
